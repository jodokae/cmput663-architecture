import json
import numpy as np
import matplotlib.pyplot as plt
import os
from scipy.stats.stats import pearsonr 
from scipy.stats.stats import spearmanr
from sklearn.model_selection import train_test_split
from sklearn import ensemble
from sklearn.dummy import DummyClassifier
from sklearn.linear_model import LinearRegression
from sklearn.metrics import confusion_matrix
from sklearn.metrics import accuracy_score
from sklearn.metrics import cohen_kappa_score
from prettytable import PrettyTable

def getBuildResults(database):
    with open(database) as json_data:
        t = json.load(json_data)

    statuses = t['statuses']

    clearedStats = []
    firstIt = True
    for k, v in sorted(statuses.items(), key=lambda statuses: int(statuses[0])):
        if firstIt:
            firstIt = False
            continue
        clearedStats.append(int(v=='passed'))
    
    return clearedStats
    

def getCompilableStats(databaseFile, compFile):
    with open(databaseFile) as json_data:
        d = json.load(json_data)

    commits = d['commits']
    
    with open(compFile) as json_data:
        c = json.load(json_data)
       
    compList = [] 
    firstIt = True
    for k, v in sorted(commits.items(), key=lambda commits: int(commits[0])):
        if firstIt:
            firstIt = False
            continue
        y = next((x for x in c if x['commitID'] == v), None)
        compList.append(int(y['compilable']))
    return compList
        
    
def getVersionDiff(versionDiffFile):
    with open(versionDiffFile) as json_data:
        d = json.load(json_data)
             
    a2a = []
    relInst = []
    absInst = []
    cvgFrom = []
    cvgTarget = []
    numNodes = []
    numEdges = []
    deg = []
    for k in sorted(d, key=lambda d: d['toVersion']):
        
        relInst.append(float(k['metrics']['global']['avgRelInst']))
        absInst.append(float(k['metrics']['global']['avgAbsInst']))
        a2a.append(float(k['metrics']['arcade']['a2a']))
        cvgFrom.append(float(k['metrics']['arcade']['cvgSource']))
        cvgTarget.append(float(k['metrics']['arcade']['cvgTarget']))
        numNodes.append(float(k['metrics']['global']['numNodes']))
        numEdges.append(float(k['metrics']['global']['numEdges']))
        deg.append(float(k['metrics']['global']['avgNodeDeg']))

    a = np.zeros((len(relInst), 8))
    for i in range(len(relInst)):
        a[i][0] = numNodes[i]
        a[i][1] = numEdges[i]
        a[i][2] = absInst[i]
        a[i][3] = relInst[i]
        a[i][4] = deg[i]
        a[i][5] = a2a[i]
        a[i][6] = cvgFrom[i]
        a[i][7] = cvgTarget[i]
        
    return a
    
def getPrevNext(y, threshold):
    # Biased because of Cross Projects
    afterStats = []
    for k in range(len(y)):
        sum = 0
        for i in range(threshold):
            if (k+i) < len(y)-1:
                sum += y[k+i]
        afterStats.append(sum)

    beforeStats = []
    for k in range(len(y)):
        sum = 0
        for i in range(threshold):
            if (k-i) > 0:
                sum += y[k-i]
        beforeStats.append(sum)
        
    return (beforeStats, afterStats)
    
def getStatistics(A, y):

    prNx_threshold = [2, 3, 5, 10]
    change_threshold = [0, 0.01, 0.02, 0.03, 0.04, 0.05, 0.1, 0.2, 0.5]

    for feature in range(A.shape[1]):
        print('\n')
        print('#'*150)
        print(featureList[feature])
        
        samples = A[:, feature]
        
        print('M vs Out ' + str(spearmanr(samples, y)))
        
        for ch_th in change_threshold:
            B = (A[:,feature]>ch_th).astype(int)
            print('Changes over Threshold ' + str(ch_th) + ': ' + str((B == 1).sum())) 
            if ((B==1).sum()) > 0:
                print('Ch (' + str(ch_th) + ') vs Out : ' + str(spearmanr(B, y)))
        
        for pr_th in prNx_threshold:
            (before, after) = getPrevNext(y, pr_th)
            print('M vs Bef (' + str(pr_th) + '): ' + str(pearsonr(samples[pr_th:], before[pr_th:])))
            print('M vs Nxt (' + str(pr_th) + '): ' + str(pearsonr(samples[pr_th:], after[pr_th:])))
            
            for ch_th in change_threshold:
                B = (A[:,feature]>ch_th).astype(int)
                if ((B==1).sum()) > 0:
                    print('Ch (' + str(ch_th) + ') vs Bef (' + str(pr_th) + '): ' + str(spearmanr(B[pr_th:], before[pr_th:])))
                    print('Ch (' + str(ch_th) + ') vs Nxt (' + str(pr_th) + '): ' + str(spearmanr(B[pr_th:], after[pr_th:])))
        
        print('#'*150)
        
def plotSpecific(A, y):
    change_threshold = np.arange(100) * 0.01
    samples = A[:, 6]
    (before, after) = getPrevNext(y, 5)
    corr = []
    for ch_th in change_threshold:
        B = (samples>ch_th).astype(int)
        (co, p) = spearmanr(B[5:], after[5:])
        corr.append(co)
    plt.plot(change_threshold, corr)
    plt.xlim([0, 1])
    plt.ylabel('Correlation')
    plt.xlabel('Change Threshold')
    plt.title('Spearman: ' + featureList[6] + ' vs Next 5 Builds')
    plt.show()
    
def machineLearn(A, y):

    X_train, X_test, y_train, y_test = train_test_split(A, y, test_size=0.33, stratify=y)

    clf = ensemble.RandomForestClassifier()
    clf.fit(X_train, y_train)
    pred = clf.predict(X_test)

    tn, fp, fn, tp = confusion_matrix(y_test, pred).ravel()
    print ( accuracy_score(y_test, pred))
    print (cohen_kappa_score(y_test, pred))

    print('TN: ' + str(tn))
    print('TP: ' + str(tp))
    print('FP: ' + str(fp))
    print('FN: ' + str(fn))
    
def plot(A):
    binwidth=0.01
    #maximum = [350, 400, 300, 200, 300, 700, 1500 , 1500]
    for feature in range(A.shape[1]):
        values = A[:, feature]
        values = list(filter(lambda a: a != 0, values))
        #print(featureList[feature])
        #print('Min: ' + str(min(A[:, feature])))
        #print('Max: ' + str(max(A[:, feature])))
        plt.hist(values, bins=np.arange(min(values), max(values) + binwidth, binwidth))
        plt.xlim([0,1])
        #plt.ylim([0, maximum[feature]])
        plt.xlabel('Change percentage')
        plt.ylabel('# Builds')
        plt.title(featureList[feature])
        #plt.show()
        plt.savefig(featureList[feature] + '.pdf')
        plt.close()

def metricCorr(A):
    t = PrettyTable()
    t.field_names = [''] + featureList

    for i in range(8):
        row = [featureList[i]] + ['', '', '', '', '', '', '', ''] 
        for j in range(8):
        
            (v, p) = pearsonr(A[:,i], A[:,j])
            row[j+1] = format(v, '.2g') + ', ' + format(p, '.2g')
            
        t.add_row(row)
    print(t)
    
def getOutlier(A):
    degree = A[:, 7]
    print(degree.shape)
    
    print(-np.partition(-degree, 3)[:3])
    
def checkCompVsBuildRes(results, comp):
    results = 1-results

    booleanComp = np.copy(comp)
    booleanComp[comp > 1] = 1
    
    res = []
    for i, val in enumerate(results):
        if val == 0:
            res.append(booleanComp[i] == 0)
        else: 
            res.append(True)
    return np.array(res)

def countCompileAndDependencyErrors(comp):
    return (comp == 2).sum() + (comp == 1).sum()
    
def countErrors(comp):
    return np.count_nonzero(comp != 0)
    
def is_non_zero_file(fpath):  
    # Five bytes to counter empty json arrays
    return os.path.isfile(fpath) and os.path.getsize(fpath) > 5

featureList = ['NumNodes', 'NumEdges', 'AbsInst', 'RelInst', 
    'NodeDegree', 'a2a', 'cvgSource', 'cvgTarget']

#0 = NO comp error
#1 = build passed

buildResults = []
comp = []
for project in os.listdir('combined/'):
    databaseFile = 'combined/' + project + '/database.json'
    compilableFile = 'combined/' + project + '/compilable.json'
    buildResults.append(getBuildResults(databaseFile))
    comp.append(getCompilableStats(databaseFile, compilableFile))
buildResults = np.array([z for x in buildResults for z in x])
comp = np.array([z for x in comp for z in x])

print(len(comp))
print(countErrors(comp))
print(countCompileAndDependencyErrors(comp))
print((comp == 0).sum())
print((comp == 1).sum())
print((comp == 2).sum())
print((comp == 3).sum())
print((comp == 4).sum())
print((checkCompVsBuildRes(buildResults, comp) == False).sum())
print((buildResults == 0).sum())

A = []
y = []
c = []
numProjects = 0

for project in os.listdir('combined/'):
    versionDiffFile = 'combined/' + project + '/versionDiff.json'
    databaseFile = 'combined/' + project + '/database.json'
    compilableFile = 'combined/' + project + '/compilable.json'
    
    if is_non_zero_file(versionDiffFile):
        numProjects += 1
        A.append(getVersionDiff(versionDiffFile))
        y.append(getBuildResults(databaseFile))
        c.append(getCompilableStats(databaseFile, compilableFile))
A = np.array([z for x in A for z in x])
y = np.array([z for x in y for z in x])
c = np.array([z for x in c for z in x])

print(numProjects)
#c[c == 2] = 1
#c[c == 3] = 0
#print(c)

passed = 0
for i in range(len(y)):
    if y[i] == True:
        passed += 1
        
print(str(passed) + ' / ' + str(len(y)))
print('Passes: ' + str(passed / len(y)))

print(np.count_nonzero(A, axis=0) / passed)

#metricCorr(A)
#getStatistics(A, c)
#machineLearn(A, y)
#plot(A)
plotSpecific(A, y)



import json
import numpy as np
from scipy.stats.stats import pearsonr 
from scipy.stats.stats import spearmanr
from scipy.stats import pointbiserialr
from scipy.stats import kendalltau

def getData(versionDiff, database): 
    with open(database) as json_data:
        d = json.load(json_data)
        
    with open(versionDiff) as json_data:
        t = json.load(json_data)

    statuses = t['statuses']

    clearedStats = []
    firstIt = True
    for k, v in sorted(statuses.items(), key=lambda statuses: int(statuses[0])):
        if firstIt:
            firstIt = False
            continue
        clearedStats.append(int(v=='passed'))

     
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
        
    return (a, clearedStats)
    
def getResults(A, y):

    for feature in range(A.shape[1]):
        print(featureList[feature] + ': ' + str(spearmanr(A[:,feature], y)))
        #plt.scatter(y, A[:,feature])
        #plt.show()

    '''
    (p,b) = kendalltau(afterStats, relInst)
    print(str(p) + ' ' + str(b))
        
    maxNumNodes = 0
    for i in range(len(d)):
        if d[i]['metrics']['global']['numNodes'] > maxNumNodes:
            maxNumNodes = d[i]['metrics']['global']['numNodes']
    #print (maxNumNodes)

    funnySum = np.zeros(len(relInst))
    for i in range(len(relInst)):
        for j in range(8):
            funnySum[i] += a[i,j] * np.power(10, j)

    print(len(funnySum))
    print(len(afterStats))
    (p, b) = pearsonr(afterStats, funnySum)
    print(str(p) + ' ' + str(b))
    '''

    from sklearn.model_selection import train_test_split

    from sklearn import ensemble
    from sklearn.dummy import DummyClassifier
    from sklearn.linear_model import LinearRegression

    X_train, X_test, y_train, y_test = train_test_split(A, y, test_size=0.33, stratify=y)

    #clf = DummyClassifier()
    clf = ensemble.RandomForestClassifier()
    clf.fit(X_train, y_train)
    pred = clf.predict(X_test)

    from sklearn import metrics

    #print(metrics.r2_score(y_test, pred))


    from sklearn.metrics import confusion_matrix
    from sklearn.metrics import accuracy_score
    from sklearn.metrics import cohen_kappa_score
    #tn, fp, fn, tp = confusion_matrix(y_test, pred).ravel()
    #print ( accuracy_score(y_test, pred))
    #print (cohen_kappa_score(y_test, pred))

    #print('TN: ' + str(tn))
    #print('TP: ' + str(tp))
    #print('FP: ' + str(fp))
    #print('FN: ' + str(fn))

featureList = ['NumNodes', 'NumEdges', 'AbsInst', 'RelInst', 
    'NodeDegree', 'a2a', 'cvgSource', 'cvgTarget']

A1, y1 = getData('database/sonarqube.json', 'diffs/versionDiff-sonarqube.json')
A2, y2 = getData('database/graylog2-server.json', 'diffs/versionDiff-graylog.json')
A3, y3 = getData('database/okhttp.json', 'diffs/versionDiff-okhttp.json')
A4, y4 = getData('database/cloudify.json', 'diffs/versionDiff-cloudify.json')
A5, y5 = getData('database/structr.json', 'diffs/versionDiff-structr.json')
A6, y6 = getData('database/owlapi.json', 'diffs/versionDiff-owlapi.json')
A7, y7 = getData('database/jOOQ.json', 'diffs/versionDiff-jooq.json')
A8, y8 = getData('database/checkstyle.json', 'diffs/versionDiff-checkstyle.json')
A9, y9 = getData('database/vectorz.json', 'diffs/versionDiff-vectorz.json')
A10, y10 = getData('database/java-driver.json', 'diffs/versionDiff-javaDriver.json')
    
A = np.concatenate((A1, A2, A3, A4, A5, A6, A7, A8, A9, A10), axis=0)
y = y1 + y2 + y3 + y4 + y5 + y6 + y7 + y8 + y9 + y10
#A = A8
#y = y8

passed = 0
for i in range(len(y)):
    if y[i] == True:
        passed += 1
        
print(str(passed) + ' / ' + str(len(y)))
print('Passes: ' + str(passed / len(y)))

# Biased because of Cross Projects
afterStats = []
for k in range(len(y)):
    sum = 0
    for i in range(10):
        if (k+i) < len(y)-1:
            if y[k+i] == 0:
                sum += 1
    afterStats.append(sum)

beforeStats = []
for k in range(len(y)):
    sum = 0
    for i in range(3):
        if (k-i) > 0:
            if y[k-i] == 0:
                sum += 1
    beforeStats.append(sum)


getResults(A, afterStats)


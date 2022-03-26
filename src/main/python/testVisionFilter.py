from numpy import average
from multiCameraServer import filterHubTargets,configX,configY
import plotly.graph_objects as go

lines = []
#input CSV is an WpiLib Data export 
with open('C:/Users/racve/OneDrive/Documents/FIRST/FRC_20220322_011433.csv') as f:
    lines = f.readlines()

count = 0
baseTime = 0
loopTime = 0.05

times = []
outX = []
outY = []
baseX = []
baseY = []
targetXarray = []
targetYarray = []

for line in lines[1:]:
    cols = line.split(',')
    if(cols[1] == '"NT:/vision/targetX"'):
        targetXarray = []
        temp = cols[2].split(';')
        for item in temp:
            if(item != '\n'):
                targetXarray.append(float(item))
    elif (cols[1] == '"NT:/vision/targetY"'):
        targetYarray = []
        temp = cols[2].split(';')
        for item in temp:
            if(item != '\n'):
                targetYarray.append(float(item))
    else:
        #unknown name of data, continue
        continue

    curTime = float(cols[0])
    while((baseTime + loopTime) < curTime):
        if(len(targetXarray) == 1):
            baseX.append(targetXarray[0])
        elif(len(targetXarray) > 1):
            baseX.append(average(targetXarray))
        elif(len(baseX) == 0):
            baseX.append(0)
        else:
            #append the last item
            baseX.append(baseX[:1])

        if(len(targetYarray) == 1):
            baseY.append(targetYarray[0])
        elif(len(targetYarray) > 1):
            baseY.append(average(targetYarray))
        elif(len(baseY) == 0):
            baseY.append(0)
        else:
            #append the last item
            baseY.append(baseY[:1])
        
        newX, configX = filterHubTargets(targetXarray, configX)
        newY, configY = filterHubTargets(targetYarray, configY)
        times.append(baseTime)
        outX.append(newX)
        outY.append(newY)
        baseTime = baseTime + loopTime
    
#actual plotting
fig = go.Figure()
fig.add_trace(go.Scatter(x=times,y=baseX,name = 'BaseLine'))
fig.add_trace(go.Scatter(x=times,y=outX,name = 'TargetX'))
fig.show()

fig = go.Figure()
fig.add_trace(go.Scatter(x=times,y=baseY,name = 'BaseLine'))
fig.add_trace(go.Scatter(x=times,y=outY,name = 'TargetY'))
fig.show()

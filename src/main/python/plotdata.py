import plotly.graph_objects as go
from datalog import DataLogReader
from tkinter import filedialog
# must run 'pip install plotly.express' first!

# reads numbers from log file
def readNumber(reader, name):
    # preallocate array for 50 loops for a hour
    MAX = 180000
    times = [0] * MAX
    values = [0] * MAX
    id = -1
    i=0

    for record in reader:
        timestamp = record.timestamp / 1000000
        if record.isStart():
            try:
                #see if this entry matches the name we need
                data = record.getStartData()
                if(data.name == name):
                    id = data.entry
            except TypeError as e:
                print("Start(INVALID)")
        #do nothing with these
        elif record.isFinish():
            e=1
        elif record.isSetMetadata():
            e=1
        elif record.isControl():
            e=1
        else:
            # if the name matches the 
            if(record.entry == id):
                times[i] = timestamp
                values[i] = record.getDouble()
                i = i+1
    
    return times[0:i],values[0:i]


if __name__ == "__main__":
    import mmap
    import sys

    file_name=filedialog.askopenfilename(title="Open WpiLog File",
                               filetypes=(("WpiLog File", "*.wpilog"), ("All Files", "*.*")))

    with open(file_name, "r") as f:
        mm = mmap.mmap(f.fileno(), 0, access=mmap.ACCESS_READ)
        reader = DataLogReader(mm)
        if not reader:
            print("not a log file", file=sys.stderr)
            sys.exit(1)
        
        #actual plotting
        fig = go.Figure()
        fig.update_layout(title_text=file_name)
        x,y = readNumber(reader,"NT:/SmartDashboard/Shot Distance")
        fig.add_trace(go.Scatter(x=x,y=y,name = 'Shot Distance'))
        x,y = readNumber(reader,"NT:/SmartDashboard/Calc RPM")
        fig.add_trace(go.Scatter(x=x,y=y,name = 'Calc RPM'))
        x,y = readNumber(reader,"NT:/SmartDashboard/Calc Hood Angle")
        fig.add_trace(go.Scatter(x=x,y=y,name = 'Calc Hood Angle'))
        fig.show()

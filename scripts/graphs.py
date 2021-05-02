import matplotlib.pyplot as plt
import os

for a in range(7):
        d=dict()
        for b in range(1,11):
                file="worker"+str(a)+"_"+str(b)+".txt"
                a_file = open("/Users/shreyasbs/Desktop/Capstone/dumps/UseParallelGCMaxTenuringThreshold=15/"+file, "r")
                lines = a_file.readlines()
                a_file.close()

                for line in range(3,len(lines)-1):
                        i=lines[line].split()
                        if i[3] in d:
                                d[i[3]].append(int(i[2]))
                        else:
                                d[i[3]]=[int(i[2])]
        
        labels = [1,2,3,4,5,6,7,8,9,10]
        width = 0.35
        fig, ax =plt.subplots()
        res = [[key, val] for key, val in d.items()]

        res.sort(key=lambda x: -x[1][0])
        print(res)
        
        ax.bar(labels,res[0][1],width,label=res[0][0])
        
        l=[0,0,0,0,0,0,0,0,0,0]
        names=[]
        for i in range(1,6):
                l = [l[j] + res[i-1][1][j] for j in range(len(l))]
                ax.bar(labels,res[i][1],width,bottom=l,label=res[i][0])
                print(res[i][0])

                #Line graph
                #plt.plot(labels,res[i][1])
                
                names.append(res[i][0])
                
        plt.title("worker"+str(a))
        plt.xlabel("time (in mins)")
        plt.ylabel("number of objects present (1e7)")
        plt.legend(names)
        plt.show()
    	
        
    


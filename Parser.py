#!/usr/bin/env python
# coding: utf-8

# In[1]:


import pandas as pd
import numpy as np
import re


# In[2]:


func_df = pd.read_csv('tFunc.csv', header = 0)


# In[3]:


def func_parser(row, col, pattern):
    temp = re.findall(pattern, row[col])
    temp = np.unique(np.array(temp))
    row['adjacentFunc'] = temp
    return row


# In[4]:


adjacentFunc_df = func_df.apply(func_parser, args= ('Function', r'\$\w+'), axis = 1)


# In[5]:


adjacentFunc_df.to_csv('func_parsed.csv', index = False)


# In[7]:


#Python program to print topological sorting of a DAG 
from collections import defaultdict 
  
#Class to represent a graph 
class Graph: 
    def __init__(self,vertices): 
        self.graph = defaultdict(list) #dictionary containing adjacency List 
        self.V = vertices #No. of vertices 
  
    # function to add an edge to graph 
    def addEdge(self,u,v): 
        self.graph[u].append(v) 
  
    # A recursive function used by topologicalSort 
    def topologicalSortUtil(self,v,visited,stack): 
  
        # Mark the current node as visited. 
        visited[v] = True
  
        # Recur for all the vertices adjacent to this vertex 
        for i in self.graph[v]: 
            if visited[i] == False: 
                self.topologicalSortUtil(i,visited,stack) 
  
        # Push current vertex to stack which stores result 
        stack.insert(0,v) 
  
    # The function to do Topological Sort. It uses recursive  
    # topologicalSortUtil() 
    def topologicalSort(self): 
        # Mark all the vertices as not visited 
        visited = [False]*self.V 
        stack =[] 
  
        # Call the recursive helper function to store Topological 
        # Sort starting from all vertices one by one 
        for i in range(self.V): 
            if visited[i] == False: 
                self.topologicalSortUtil(i,visited,stack) 
  
        # Print contents of stack 
        print(stack)
  


# In[8]:


g = Graph(6) 
g.addEdge(5, 2); 
g.addEdge(5, 0); 
g.addEdge(4, 0); 
g.addEdge(4, 1); 
g.addEdge(2, 3); 
g.addEdge(3, 1); 


# In[10]:


g.topologicalSort() 


# In[13]:


import networkx as nx


# In[14]:


a = np.random.randint(0, 2, size=(10, 10))
D = nx.DiGraph(a)


# In[39]:


# Add a vertex to the dictionary
def add_vertex(v):
    global graph
    global vertices_no
    if v in graph:
        print("Vertex ", v, " already exists.")
    else:
        vertices_no = vertices_no + 1
        graph[v] = []

# Add an edge between vertex v1 and v2 with edge weight e
def add_edge(v1, v2):
    global graph
    # Check if vertex v1 is a valid vertex
    if v1 not in graph:
        print("Vertex ", v1, " does not exist.")
    # Check if vertex v2 is a valid vertex
    elif v2 not in graph:
        print("Vertex ", v2, " does not exist.")
    else:
        # Since this code is not restricted to a directed or 
        # an undirected graph, an edge between v1 v2 does not
        # imply that an edge exists between v2 and v1
        temp = [v2]
        graph[v1].append(temp)

# Print the graph
def print_graph():
    global graph
    for vertex in graph:
        for edges in graph[vertex]:
            print(vertex, " -> ", edges[0])


# In[ ]:





# In[41]:


# driver code
graph = {}
# stores the number of vertices in the graph
vertices_no = 0
add_vertex(1)
add_vertex(2)
add_vertex(3)
add_vertex(4)
# Add the edges between the vertices by specifying
# the from and to vertex along with the edge weights.
add_edge(1, 2)
add_edge(1, 3)
add_edge(2, 3)
add_edge(3, 4)
add_edge(4, 1)
print_graph()
# Reminder: the second element of each list inside the dictionary
# denotes the edge weight.
print ("Internal representation: ", graph)


# In[29]:


adjacentFunc_df[['ItemName', 'adjacentFunc']].head()


# In[42]:


# driver code
graph = {}
# stores the number of vertices in the graph
vertices_no = 0
add_vertex(adjacentFunc_df['ItemName'][0])
add_vertex(adjacentFunc_df['adjacentFunc'][0][0])
add_edge(adjacentFunc_df['ItemName'][0], adjacentFunc_df['adjacentFunc'][0][0], )


# In[43]:


graph


# In[ ]:


def add_all_vertices(row):
    add_vertex(row[])


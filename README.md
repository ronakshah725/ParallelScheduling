Program Dependencies

****APPROACH****

1. Our task is to determine order of execution of N programs, given the dependencies of each, such that all programs 
   with zero dependencies can be executed in parallel.

2. This can be thought of as a Directed Acyclic Graph G with programs as nodes and an edge from Node u -> Node v 
   representing, v depends on u. 

3. The topological ordering of G (with some tweaking) gives the order of parallel execution.

4. For topological ordering, a Breadth First Search approach is used.

5. If there is a cyclic dependency, like A depends on B & B depends on A, the algorithm stops.

5. Optionally, a concurrency limit can be specified, which specifies maximum programs that can be processed in parallel.

****RUN****

1. ProgramName: Dependencies.java
2. Compile javac Dependencies.java
3. Run:java Dependencies conf-file [conc-limit]
4. Sample conf files : dep.conf, dep2.conf and cycle.conf(consists of cyclic dependencies)

****FORMAT OF CONFIGURATION FILE****

[Program name] : [Dependent Program 1, Dependent Program 2 ...]

Comments can be added by beginning line with #


Sample Configuration:

A : 

B : A, F

C : A, F

D : A

F : 

E : B, C, D

Sample Output:

Process order:

AF

CBD

E

Done !!

Features

1. Concurreny Limit
2. Cycle Detection
3. Reguluar Expression parsing of conf file


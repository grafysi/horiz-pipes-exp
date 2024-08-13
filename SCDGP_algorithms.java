Definitions: A[n] is a record of type A and have lsn n and
            A[n].key is primary key of A[n]
            A[n].B is reference key of A[n] to B[m]
            Source[A] is a stream of records of type A
            Subject is a marker that indicate a record type is cluster center
            Subjects[i] is a subject in a list of Subjects
            Graphs[i] is a graph in a list of Graphs and have root at Subject[i]
            dispatch(A[n], r) is a function that dispatch record A[n] to route r


---
        for A[n] in A.source
            if A is A.subject
                if A[n].key in A.graph.vertices
                    dispatch(A[n], Graphs[i].route)
                if A[n].key is not in Graphs[i].vertices

        for i = 1 to n
            if A = A.subject
                if A[i].key in A.dgraph.vertices
                    dispatch(A[n], A.dgraph.route)









---
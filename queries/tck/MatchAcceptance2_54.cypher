MATCH ()-[r1]->()
WITH r1 AS r2, count(*) AS c
ORDER BY c
MATCH ()-[r2]->()
RETURN r2 AS rel

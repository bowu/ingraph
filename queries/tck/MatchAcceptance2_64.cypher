MATCH (a)-[r1]->()-[r2]->(b)
WITH [r1, r2] AS rs, a AS second, b AS first
LIMIT 1
MATCH (first)-[rs*]->(second)
RETURN first, second


      MATCH (a1)-[r]->()
      WITH r, a1
        LIMIT 1
      MATCH (a1:X)-[r]->(b2)
      RETURN a1, r, b2
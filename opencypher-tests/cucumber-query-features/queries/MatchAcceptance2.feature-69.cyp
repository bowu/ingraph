
      MATCH (a1)-[r]->()
      WITH r, a1
        LIMIT 1
      OPTIONAL MATCH (a2)<-[r]-(b2)
      WHERE a1 = a2
      RETURN a1, r, b2, a2
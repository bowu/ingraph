
      MATCH (a)
      WHERE NOT a.name STARTS WITH null
      RETURN a
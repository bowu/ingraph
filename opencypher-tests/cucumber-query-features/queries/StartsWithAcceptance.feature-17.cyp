
      MATCH (a)
      WHERE NOT a.name ENDS WITH null
      RETURN a

      MATCH (n)
      UNWIND keys(n) AS x
      RETURN DISTINCT x AS theProps
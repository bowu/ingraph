
      MATCH (a)-->(b)
      RETURN DISTINCT b
        ORDER BY b.name

      MATCH (a)
      RETURN a.count
        ORDER BY a.count
        SKIP 10
        LIMIT 10
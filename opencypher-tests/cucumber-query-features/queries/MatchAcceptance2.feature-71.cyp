
      MATCH (foo)
      RETURN foo.bar AS x
        ORDER BY x DESC
        LIMIT 4

      UNWIND range(1000000, 2000000) AS i
      WITH i
      LIMIT 3000
      RETURN sum(i)
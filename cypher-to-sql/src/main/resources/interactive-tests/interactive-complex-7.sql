select p_personid, p_firstname, p_lastname, l.l_creationdate, m_messageid,
	COALESCE(m_ps_imagefile,'')||COALESCE(m_content,''),
	EXTRACT(EPOCH FROM (to_timestamp(l.l_creationdate) - to_timestamp(m_creationdate))) / 60 as lag,
    (case when exists (select 1 from knows_undirected where k_person1id = 26388279067534 and k_person2id = p_personid) then 0 else 1 end) as isnew
from
  (select l_personid, max(l_creationdate) as l_creationdate
   from likes, message
   where
     m_messageid = l_messageid and
     m_creatorid = 26388279067534
   group by l_personid
   order by 2 desc
   limit 20
  ) tmp, message, person, likes as l
where
	p_personid = tmp.l_personid and
	tmp.l_personid = l.l_personid and
	tmp.l_creationdate = l.l_creationdate and
	l.l_messageid = m_messageid
order by 4 desc, 1

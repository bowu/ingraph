#!/bin/bash

$NEO4J_HOME/bin/neo4j-import --into $DB_DIR \
  --nodes:Message:Comment comment$POSTFIX \
  --nodes:Forum forum$POSTFIX \
  --nodes:Organisation organisation$POSTFIX \
  --nodes:Person person$POSTFIX \
  --nodes:Place place$POSTFIX \
  --nodes:Message:Post post$POSTFIX \
  --nodes:TagClass tagclass$POSTFIX \
  --nodes:Tag tag$POSTFIX \
  --relationships:hasCreator comment_hasCreator_person$POSTFIX \
  --relationships:isLocatedIn comment_isLocatedIn_place$POSTFIX \
  --relationships:replyOf comment_replyOf_comment$POSTFIX \
  --relationships:replyOf comment_replyOf_post$POSTFIX \
  --relationships:containerOf forum_containerOf_post$POSTFIX \
  --relationships:hasMember forum_hasMember_person$POSTFIX \
  --relationships:hasModerator forum_hasModerator_person$POSTFIX \
  --relationships:hasTag forum_hasTag_tag$POSTFIX \
  --relationships:hasInterest person_hasInterest_tag$POSTFIX \
  --relationships:isLocatedIn person_isLocatedIn_place$POSTFIX \
  --relationships:knows person_knows_person$POSTFIX \
  --relationships:likes person_likes_comment$POSTFIX \
  --relationships:likes person_likes_post$POSTFIX \
  --relationships:isPartOf place_isPartOf_place$POSTFIX \
  --relationships:hasCreator post_hasCreator_person$POSTFIX \
  --relationships:hasTag comment_hasTag_tag$POSTFIX \
  --relationships:hasTag post_hasTag_tag$POSTFIX \
  --relationships:isLocatedIn post_isLocatedIn_place$POSTFIX \
  --relationships:isSubclassOf tagclass_isSubclassOf_tagclass$POSTFIX \
  --relationships:hasType tag_hasType_tagclass$POSTFIX \
  --relationships:studyAt person_studyAt_organisation$POSTFIX \
  --relationships:workAt person_workAt_organisation$POSTFIX \
  --relationships:isLocatedIn organisation_isLocatedIn_place$POSTFIX \
  --delimiter '|'

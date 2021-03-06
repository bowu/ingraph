package ingraph.ire.nodes.binary

import ingraph.ire.datatypes.Slot._
import ingraph.ire.datatypes._
import ingraph.ire.messages.{ChangeSet, ReteMessage, SingleForwarder}

class LeftOuterJoinNode(override val next: (ReteMessage) => Unit,
                        override val primaryTupleWidth: Int,
                        override val secondaryTupleWidth: Int,
                        override val primaryMask: Mask,
                        override val secondaryMask: Mask)
  extends JoinNodeBase with SingleForwarder {

  val pairlessTuples = new JoinCache

  override def onSizeRequest(): Long = 0

  override def onPrimary(changeSet: ChangeSet): Unit = {
    forward(ChangeSet(
      positive = changeSet.positive.flatMap(calculatePrimaryPositive),
      negative = changeSet.negative.flatMap(calculatePrimaryNegative)
    ))
  }

  override def onSecondary(changeSet: ChangeSet): Unit = {
    val positiveUpdatesChangeSet = changeSet.positive.map(calculateSecondaryPositive).reduceOption(combineChangeSets).getOrElse(ChangeSet())
    val negativeUpdatesChangeSet = changeSet.negative.map(calculateSecondaryNegative).reduceOption(combineChangeSets).getOrElse(ChangeSet())

    forward(combineChangeSets(positiveUpdatesChangeSet, negativeUpdatesChangeSet))
  }

  private def calculatePrimaryPositive(inputTuple: Tuple): Iterable[Tuple] = {
    val joinedPositiveTuples: Iterable[Tuple] = joinTuples(Vector(inputTuple), secondaryIndexer, primaryMask, Primary)
    val joinAttributesTuple = extract(inputTuple, primaryMask)

    primaryIndexer.addBinding(joinAttributesTuple, inputTuple)

    if(joinedPositiveTuples.isEmpty){
      pairlessTuples.addBinding(joinAttributesTuple, inputTuple)
      return Vector(padWithNull(inputTuple))
    }

    joinedPositiveTuples
  }

  private def calculatePrimaryNegative(inputTuple: Tuple): Iterable[Tuple] = {
    val joinAttributesTuple = extract(inputTuple, primaryMask)

    primaryIndexer.removeBinding(joinAttributesTuple, inputTuple)

    if(pairlessTuples.entryExists(joinAttributesTuple, _ == inputTuple)){
      pairlessTuples.removeBinding(joinAttributesTuple, inputTuple)
      return Vector(padWithNull(inputTuple))
    }

    joinTuples(Vector(inputTuple), secondaryIndexer, primaryMask, Primary)
  }

  private def calculateSecondaryPositive(inputTuple: Tuple): ChangeSet = {
    val joinedPositiveTuples = joinTuples(Vector(inputTuple), primaryIndexer, secondaryMask, Secondary)

    var negativeTuples: Vector[Tuple] = Vector()

    val joinAttributesTuple = extract(inputTuple, secondaryMask)
    if(pairlessTuples.contains(joinAttributesTuple)){
      negativeTuples = pairlessTuples.remove(joinAttributesTuple).get.map(padWithNull).toVector
    }

    secondaryIndexer.addBinding(joinAttributesTuple, inputTuple)

    ChangeSet(positive = joinedPositiveTuples.toVector, negative = negativeTuples)
  }

  private def calculateSecondaryNegative(inputTuple: Tuple): ChangeSet = {
    val joinedNegativeTuples = joinTuples(Vector(inputTuple), primaryIndexer, secondaryMask, Secondary)

    val joinAttributesTuple = extract(inputTuple, secondaryMask)
    val positiveTuples = if (
      joinedNegativeTuples.nonEmpty && secondaryIndexer(joinAttributesTuple).size <= 1){
      val pairsFromPrimary = primaryIndexer(joinAttributesTuple)
      pairlessTuples.put(joinAttributesTuple, pairsFromPrimary)
      pairsFromPrimary.map(padWithNull).toVector
    } else Vector.empty[Tuple]

    secondaryIndexer.removeBinding(joinAttributesTuple, inputTuple)

    ChangeSet(positive=positiveTuples, negative=joinedNegativeTuples.toVector)
  }

  private val nullFiller = Vector.fill(secondaryTupleWidth - secondaryMask.size)(null)
  private def padWithNull(inputTuple: Tuple): Tuple = {
    inputTuple ++ nullFiller
  }

  private def combineChangeSets(cs1: ChangeSet, cs2: ChangeSet): ChangeSet = {
    ChangeSet(positive = cs1.positive ++ cs2.positive, negative = cs1.negative ++ cs2.negative)
  }

}


class ThetaLeftOuterJoinNode(override val next: ReteMessage => Unit,
                        override val primaryTupleWidth: Int,
                        override val secondaryTupleWidth: Int,
                        override val primaryMask: Mask,
                        override val secondaryMask: Mask,
                        val theta: Tuple => Boolean)
  extends LeftOuterJoinNode(next, primaryTupleWidth, secondaryTupleWidth, primaryMask, secondaryMask) {
  override def joinTuples(tuples: Vector[Tuple], otherIndexer: JoinCache,
                          slotMask: Mask, slot: Slot): Iterable[Tuple] = {
    super.joinTuples(tuples, otherIndexer, slotMask, slot).filter(theta(_))
  }
}

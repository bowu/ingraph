package ingraph.ire.nodes.unary.aggregation

import ingraph.ire.datatypes._
import ingraph.ire.math.GenericMath

class StatefulSum(sumKey: Int) extends StatefulAggregate {
  var sum: Any = 0

  override def maintainPositive(values: Iterable[Tuple]): Unit = {
    for (tuple <- values) {
      sum = GenericMath.add(sum, tuple(sumKey))
    }
  }

  override def maintainNegative(values: Iterable[Tuple]): Unit = {
    for (tuple <- values) {
      sum = GenericMath.subtract(sum, tuple(sumKey))
    }
  }

  override def value(): Any = sum
}

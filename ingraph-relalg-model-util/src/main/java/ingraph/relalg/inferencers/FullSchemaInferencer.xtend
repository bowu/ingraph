package ingraph.relalg.inferencers

import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import ingraph.relalg.calculators.JoinAttributeCalculator
import ingraph.relalg.calculators.ListUnionCalculator
import ingraph.relalg.calculators.MaskCalculator
import ingraph.relalg.util.visitors.PostOrderTreeVisitor
import java.util.List
import relalg.AbstractJoinOperator
import relalg.NullaryOperator
import relalg.Operator
import relalg.RelalgContainer
import relalg.TernaryOperator
import relalg.UnaryOperator
import relalg.UnionOperator
import relalg.Variable

/**
 * Inferences the full schema, including extra attributes.
 */
class FullSchemaInferencer {

  extension PostOrderTreeVisitor treeVisitor = new PostOrderTreeVisitor
  extension JoinAttributeCalculator joinAttributeCalculator = new JoinAttributeCalculator
  extension ListUnionCalculator listUnionCalculator = new ListUnionCalculator
  extension MaskCalculator maskCalculator = new MaskCalculator

  def inferFullSchema(RelalgContainer container) {
    if (!container.extraAttributesInferred) {
      throw new IllegalStateException("ExtraAttributeInferencer must be executed before FullSchemaInferencer")
    } else if (container.fullSchemaInferred) {
      throw new IllegalStateException("FullSchemaInferencer on relalg container was already executed")
    } else {
      container.fullSchemaInferred = true
    }

    container.rootExpression.traverse([fillFullSchema])
    container.rootExpression.traverse([calculateTuples])
    container
  }

  /**
   * fillFullSchema
   */
  private def dispatch void fillFullSchema(NullaryOperator op) {
    val detailedSchema = union(op.schema, op.extraVariables)
    op.defineDetailedSchema(detailedSchema)
  }

  private def dispatch void fillFullSchema(UnaryOperator op) {
    val detailedSchema = op.input.detailedSchema
    op.defineDetailedSchema(detailedSchema)
  }

  private def dispatch void fillFullSchema(UnionOperator op) {
    throw new UnsupportedOperationException("Union not yet supported")
  }

  private def dispatch void fillFullSchema(AbstractJoinOperator op) {
    val schema = calculateJoinAttributes(op, op.getLeftInput.detailedSchema, op.getRightInput.detailedSchema)
    op.defineDetailedSchema(schema)
  }

  private def dispatch void fillFullSchema(TernaryOperator op) {
    val detailedSchema = Lists.newArrayList(Iterables.concat(
      op.getLeftInput.detailedSchema,
      op.getMiddleInput.detailedSchema,
      op.getRightInput.detailedSchema
    ))
    op.defineDetailedSchema(detailedSchema)
  }
  
  /**
   * defineSchema
   */
  def void defineDetailedSchema(Operator op, List<? extends Variable> detailedSchema) {
    op.detailedSchema.addAll(detailedSchema)
  }

}
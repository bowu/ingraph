package ingraph.relalg.calculators

import com.google.common.collect.Iterables
import java.util.List
import relalg.AntiJoinOperator
import relalg.EquiJoinLikeOperator
import relalg.Variable

class JoinAttributeCalculator {

	def dispatch calculateJoinAttributes(EquiJoinLikeOperator op, List<Variable> leftSchema, List<Variable> rightSchema) {
		//Lists.newArrayList(Iterables.concat(leftSchema, rightSchema))
		val leftSchemaNames = leftSchema.map[name]

		val joinAttributes = Iterables.concat( // 
			leftSchema,
			rightSchema.filter[
				variable | !leftSchemaNames.contains(variable.name) // only keep variables that are NOT in the left schema
			]
		)
		joinAttributes.toList
	}

	def dispatch calculateJoinAttributes(AntiJoinOperator op, List<Variable> leftSchema, List<Variable> rightSchema) {
		leftSchema
	}
	
}
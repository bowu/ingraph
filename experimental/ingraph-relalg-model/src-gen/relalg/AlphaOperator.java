/**
 */
package relalg;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Alpha Operator</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link relalg.AlphaOperator#getInput <em>Input</em>}</li>
 * </ul>
 *
 * @see relalg.RelalgPackage#getAlphaOperator()
 * @model abstract="true"
 * @generated
 */
public interface AlphaOperator extends Operator {
	/**
	 * Returns the value of the '<em><b>Input</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Input</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Input</em>' containment reference.
	 * @see #setInput(Operator)
	 * @see relalg.RelalgPackage#getAlphaOperator_Input()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Operator getInput();

	/**
	 * Sets the value of the '{@link relalg.AlphaOperator#getInput <em>Input</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Input</em>' containment reference.
	 * @see #getInput()
	 * @generated
	 */
	void setInput(Operator value);

} // AlphaOperator
/**
 * generated by Xtext 2.10.0
 */
package org.xtext.example.mydsl.myDsl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Pattern Part</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.xtext.example.mydsl.myDsl.PatternPart#getNode <em>Node</em>}</li>
 *   <li>{@link org.xtext.example.mydsl.myDsl.PatternPart#getChain <em>Chain</em>}</li>
 * </ul>
 *
 * @see org.xtext.example.mydsl.myDsl.MyDslPackage#getPatternPart()
 * @model
 * @generated
 */
public interface PatternPart extends EObject
{
  /**
   * Returns the value of the '<em><b>Node</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Node</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Node</em>' containment reference.
   * @see #setNode(NodePattern)
   * @see org.xtext.example.mydsl.myDsl.MyDslPackage#getPatternPart_Node()
   * @model containment="true"
   * @generated
   */
  NodePattern getNode();

  /**
   * Sets the value of the '{@link org.xtext.example.mydsl.myDsl.PatternPart#getNode <em>Node</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Node</em>' containment reference.
   * @see #getNode()
   * @generated
   */
  void setNode(NodePattern value);

  /**
   * Returns the value of the '<em><b>Chain</b></em>' containment reference list.
   * The list contents are of type {@link org.xtext.example.mydsl.myDsl.PatternElementChain}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Chain</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Chain</em>' containment reference list.
   * @see org.xtext.example.mydsl.myDsl.MyDslPackage#getPatternPart_Chain()
   * @model containment="true"
   * @generated
   */
  EList<PatternElementChain> getChain();

} // PatternPart
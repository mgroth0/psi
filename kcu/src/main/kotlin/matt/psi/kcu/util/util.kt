package matt.psi.kcu.util

import com.intellij.lang.ASTNode
import matt.prim.str.removeSurrounding
import matt.psi.hasPrivateModifier
import matt.psi.kcu.children
import matt.psi.kcu.wrapped
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.lexer.KtTokens

fun ASTNode.rawNonPrivateTopLevelDeclarations(): Sequence<ASTNode> =
    children()
        .filter { it.doesNotHavePrivateModifier() }

fun ASTNode.doesNotHavePrivateModifier() = !wrapped().hasPrivateModifier()

val NON_CLASS_RELATED_TOP_LEVEL_DECLARATION_TYPES =
    listOf(
        KtNodeTypes.OBJECT_DECLARATION,
        KtNodeTypes.TYPEALIAS,
        KtNodeTypes.PROPERTY
    )

fun ASTNode.identifier(): String? =
    findChildByType(KtTokens.IDENTIFIER)
        ?.run {
            text.removeSurrounding('`')
        }

package matt.psi.kc.glue

import matt.lang.safeconvert.verifyToUInt
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.com.intellij.psi.PsiElement

inline val TextRange.startOffsetU get() = startOffset.verifyToUInt()
inline val TextRange.endOffsetU get() = endOffset.verifyToUInt()
inline val PsiElement.startOffsetU get() = textRange.startOffsetU
@Suppress("unused")
inline val PsiElement.endOffsetU get() = textRange.endOffsetU
inline val PsiElement.textLengthU get() = textLength.verifyToUInt()
inline val ASTNode.textLengthU get() = textLength.verifyToUInt()

inline val ASTNode.startOffsetU get() = textRange.startOffsetU
inline val ASTNode.endOffsetU get() = textRange.endOffsetU

package matt.psi.kc

import matt.psi.ASTNodeWrapper
import matt.psi.KotlinModifier
import matt.psi.PsiElementWrapper
import matt.psi.children
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.psiUtil.prevLeaf

fun PsiElementWrapper.unwrapped() = (this as KcPsiElementWrapper).psiElement
fun ASTNodeWrapper.unwrapped() = (this as KcAstNodeWrapper).node
fun ASTNode.children() = wrapped().children().map { (it as KcAstNodeWrapper).node }
@Suppress("NOTHING_TO_INLINE")
inline fun PsiElement.wrapped(): PsiElementWrapper = KcPsiElementWrapper(this)

@JvmInline
@PublishedApi
internal value class KcPsiElementWrapper(
    val psiElement: PsiElement
): PsiElementWrapper {
    override val node: ASTNodeWrapper get() = KcAstNodeWrapper(psiElement.node)
    override val text: String get() = psiElement.text
    override fun prevLeaf() = psiElement.prevLeaf()?.wrapped()
    override fun prevLeaf(filter: (PsiElementWrapper) -> Boolean) =
        psiElement.prevLeaf {
            filter(it.wrapped())
        }?.wrapped()
    override fun delete() = psiElement.delete()
    override fun replace(newElement: PsiElementWrapper) = psiElement.replace(newElement.unwrapped()).wrapped()
    @Suppress("ForbiddenIsCheck")
    override fun isComment() = psiElement is PsiComment
}

@Suppress("NOTHING_TO_INLINE")
inline fun ASTNode.wrapped(): ASTNodeWrapper = KcAstNodeWrapper(this)

@JvmInline
@PublishedApi
internal value class KcAstNodeWrapper(
    val node: ASTNode
): ASTNodeWrapper {
    override val firstChildNode get() = node.firstChildNode?.wrapped()
    override val treeNext get() = node.treeNext?.wrapped()

    override fun isWhiteSpace() = node.elementType == KtTokens.WHITE_SPACE
    override fun isBlockComment() = node.elementType == KtTokens.BLOCK_COMMENT
    override fun isEolComment() = node.elementType == KtTokens.EOL_COMMENT
    override fun hasModifier(modifier: KotlinModifier): Boolean =
        node.findChildByType(KtNodeTypes.MODIFIER_LIST)
            ?.run {
                children().any {
                    it.text == modifier.name
                }
            } == true
}

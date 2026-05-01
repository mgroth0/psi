package matt.psi

fun PsiElementWrapper.isWhiteSpace() = node.isWhiteSpace()
fun PsiElementWrapper.isBlockComment() = node.isBlockComment()

interface PsiElementWrapper {
    val node: ASTNodeWrapper
    val text: String
    fun prevLeaf(): PsiElementWrapper?
    fun prevLeaf(filter: (PsiElementWrapper) -> Boolean): PsiElementWrapper?
    fun delete()
    fun replace(newElement: PsiElementWrapper): PsiElementWrapper
    fun isComment(): Boolean
}

interface ASTNodeWrapper {
    val firstChildNode: ASTNodeWrapper?
    val treeNext: ASTNodeWrapper?
    fun isWhiteSpace(): Boolean
    fun isBlockComment(): Boolean
    fun isEolComment(): Boolean
    /*this used to be a private function, but now that its in the interface it cannot be. */
    fun hasModifier(modifier: KotlinModifier): Boolean
}

enum class KotlinModifier { private, actual, expect }

fun ASTNodeWrapper.hasPrivateModifier(): Boolean = hasModifier(KotlinModifier.private)
fun ASTNodeWrapper.hasActualModifier(): Boolean = hasModifier(KotlinModifier.actual)
fun ASTNodeWrapper.hasExpectModifier(): Boolean = hasModifier(KotlinModifier.expect)

const val BLOCK_COMMENT_START = "/*"
const val BLOCK_COMMENT_END = "*/"

fun PsiElementWrapper.getBlockCommentContent(): String {
    check(isBlockComment())
    return text.removePrefix(BLOCK_COMMENT_START).removeSuffix(BLOCK_COMMENT_END)
}

/*this was copied (and since edited slightly by me) directly from ktlint. Thanks ktlint for this nice little utility.*/
fun ASTNodeWrapper.children(): Sequence<ASTNodeWrapper> = generateSequence({ firstChildNode }) { node -> node.treeNext }

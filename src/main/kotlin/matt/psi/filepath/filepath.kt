package matt.psi.filepath

import matt.file.ext.FileExtension
import matt.file.isgen.kotlinFileIsGeneratedButNotByMe
import matt.model.k.file.UnsafeFilePath
import matt.model.k.kstruct.gradle.project.GradleKSubProjectPath
import matt.model.k.kstruct.mod.SubRoot
import matt.model.k.kstruct.mod.subProject
import matt.prim.str.substringAfterSingular
import matt.prim.str.substringBeforeSingular
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.extension
import kotlin.io.path.name

/*this really doesn't belong here. Need to find better home when I can.*/
class KtFilePathInfo private constructor(
    val absolutePath: UnsafeFilePath,
    val fileName: String,
    val packageName: String,
    val packageNameKt: String,
    val isMain: Boolean,
    val expectedBasePackageNames: List<String>,
    val expectedPackageNames: List<String>,
    @Suppress("unused")
    val pathRelativeToK: String,
    @Suppress("unused") val kSubPath: GradleKSubProjectPath
) {
    companion object {

        fun parseKotlinFilePathOrNullIfNotKt(
            ktFile: KtFile
        ): KtFilePathInfo? {
            val path = Path(ktFile.virtualFilePath)
            if (!shouldRunRulesOn(path)) return null
            return parseKotlinFilePath(path)
        }

        fun shouldRunFilePathBasedRulesOn(ktFile: KtFile) = shouldRunRulesOn(Path(ktFile.virtualFilePath))
        private fun shouldRunRulesOn(filePath: Path): Boolean {
            check(filePath.isAbsolute) {
                "$filePath should be absolute"
            }
            check(filePath.getName(0).name == "Users") {
                "absolute path $filePath should start with Users"
            }
            /*
            || filePath.endsWith("package.kt")
            Why would I ignore package.kt?
            ignore all non ".kt" files (including ".kts")
             */
            return (!shouldExcludeDueToBeingGenerated(filePath))
                && filePath.extension == FileExtension.KT.afterPrefixDot
        }

        private fun shouldExcludeDueToBeingGenerated(
            filePath: Path
        ) = kotlinFileIsGeneratedButNotByMe(filePath = filePath)

        private fun parseKotlinFilePath(
            filePath: Path
        ): KtFilePathInfo {
            check(shouldRunRulesOn(filePath))
            val absPathString = filePath.toString()
            val afterK = absPathString.substringAfterSingular("/k/")
            val kToSrc = afterK.substringBeforeSingular("/src/")
            val moduleNames = kToSrc.split("/")
            val filePathNames = absPathString.split("/")
            val srcIndex = filePathNames.indexOf("src")
            require(filePathNames[srcIndex + 2] == "kotlin")
            val expectedBasePackageNames = listOf("matt") + moduleNames
            val mainKotlinFileParent = filePathNames.subList(0, srcIndex + 3) + expectedBasePackageNames
            val myParent = absPathString.substringBeforeLast("/").split("/")
            val isMain = myParent == mainKotlinFileParent
            val fileName =
                absPathString
                    .replace('\\', '/') /*Ensure compatibility with Windows OS*/
                    .substringAfterLast("/")

            val packageName = absPathString.substringBeforeLast('/').substringAfterLast('/')
            val packageNameKt = packageName + FileExtension.KT.withPrefixDot
            val expectedPackageNames = filePathNames.subList(srcIndex + 3, filePathNames.lastIndex)
            return KtFilePathInfo(
                absolutePath = UnsafeFilePath(absPathString),
                fileName = fileName,
                packageNameKt = packageNameKt,
                isMain = isMain,
                packageName = packageName,
                expectedBasePackageNames = expectedBasePackageNames,
                expectedPackageNames = expectedPackageNames,
                pathRelativeToK = afterK,
                kSubPath = SubRoot.k.subProject(*kToSrc.removePrefix("/").removeSuffix("/").split('/').toTypedArray())
            )
        }
    }
}

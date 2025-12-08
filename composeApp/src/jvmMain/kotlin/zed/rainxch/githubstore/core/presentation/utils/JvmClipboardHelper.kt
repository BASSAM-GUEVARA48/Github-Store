package zed.rainxch.githubstore.core.presentation.utils

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class JvmClipboardHelper : ClipboardHelper {
    override fun copy(label: String, text: String) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(StringSelection(text), null)
    }
}
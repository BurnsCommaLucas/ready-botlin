package io.burnscommalucas.readybotlin.model

import io.burnscommalucas.readybotlin.model.command.Command.CHECK
import io.burnscommalucas.readybotlin.model.command.Command.DOCS
import io.burnscommalucas.readybotlin.model.command.Command.READY
import io.burnscommalucas.readybotlin.model.command.Command.UNREADY
import io.burnscommalucas.readybotlin.model.command.Command.WHO

object OutboundMessaging {
    val HELP_MESSAGE =
        """
        To create a check, run `/$CHECK`
        To respond to a check, run `/$READY` or `/$UNREADY`
        To see who still needs to ready, run `/$WHO`
        If you still need help, you can come check out our Github page. Type `/$DOCS`
        """.trimIndent()

    const val DOCS_MESSAGE =
        "To get involved in the development of ready-bot or to report an issue, visit our [Github](https://github.com/BurnsCommaLucas/ready-bot)"

    const val NO_CHECK_MESSAGE = "No ready check active in this channel."

    val PLEASE_REPORT = "\n\nIf this keeps happening, please run `/$DOCS` to report this to my maker!"
}

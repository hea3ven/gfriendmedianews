import com.google.common.util.concurrent.FutureCallback
import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.Javacord
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.listener.message.MessageCreateListener

fun main(args: Array<String>) {
	val api = Javacord.getApi("", true)
	api.connect(object : FutureCallback<DiscordAPI> {
		override fun onSuccess(result: DiscordAPI?) {
			api.registerListener(MessageCreateListener { api, message ->
				if (message.getContent().toLowerCase() == "ping") {
					message.reply("pong")
				}
			})
		}

		override fun onFailure(t: Throwable?) {
			t?.printStackTrace()
		}
	})
	while (true)
		Thread.sleep(5000)
}
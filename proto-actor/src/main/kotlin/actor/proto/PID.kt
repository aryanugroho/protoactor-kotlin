package actor.proto

import actor.proto.mailbox.SystemMessage
import java.time.Duration


typealias PID = Protos.PID
fun PID(address: String, id: String): PID {
    val p = PID.newBuilder()
    p.address = address
    p.id = id
    return p.build()
}

internal fun PID.cachedProcess(): Process? {
    if (cachedProcess_ == null) {
        cachedProcess_ = ProcessRegistry.get(this)
    }
    return cachedProcess_
}

fun PID.sendSystemMessage(sys: SystemMessage) {
    val process: Process = cachedProcess() ?: ProcessRegistry.get(this)
    process.sendSystemMessage(this, sys)
}

fun PID.request(message: Any, sender: PID) {
    val process = cachedProcess() ?: ProcessRegistry.get(this)
    val messageEnvelope = MessageEnvelope(message, sender, null)
    process.sendUserMessage(this, messageEnvelope)
}

suspend fun <T> PID.requestAwait(message: Any, timeout: Duration): T = requestAwait(message, FutureProcess(timeout))

suspend fun <T> PID.requestAwait(message: Any): T = requestAwait(message, FutureProcess())

suspend private fun <T> PID.requestAwait(message: Any, future: FutureProcess<T>): T {
    request(message, future.pid)
    return future.get()
}

fun PID.toShortString(): String {
    return "$address/$id"
}

fun PID.stop() {
    val process = cachedProcess() ?: ProcessRegistry.get(this)
    process.stop(this)
}

fun PID.send(message: Any) {
    val process: Process = cachedProcess() ?: ProcessRegistry.get(this)
    process.sendUserMessage(this, message)
}

package actor.proto.router

import actor.proto.Context
import actor.proto.PID

abstract class GroupRouterConfig(private var routees: Set<PID>) : IGroupRouterConfig {
    override fun onStarted(context: Context, router: RouterState) {
        routees.forEach { context.watch(it) }
        router.setRoutees(routees)
    }
}

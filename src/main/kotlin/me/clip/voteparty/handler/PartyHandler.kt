package me.clip.voteparty.handler

import me.clip.voteparty.base.Addon
import me.clip.voteparty.base.formMessage
import me.clip.voteparty.conf.ConfigVoteParty
import me.clip.voteparty.plugin.VotePartyPlugin
import org.bukkit.entity.Player
import java.util.concurrent.ThreadLocalRandom.current

class PartyHandler(override val plugin: VotePartyPlugin) : Addon
{
	private val conf: ConfigVoteParty
		get() = party.conf()
	
	fun giveRandomPartyRewards(player: Player)
	{
		val cmds = conf.party?.rewardCommands?.commands?.takeIf { it.isNotEmpty() } ?: return
		
		repeat(conf.party?.maxRewardsPerPlayer ?: 0)
		{
			val cmd = cmds.random()
			
			if (cmd.chance <= current().nextInt(100))
			{
				server.dispatchCommand(server.consoleSender, formMessage(player, cmd.command))
			}
		}
	}
	
	fun giveGuaranteedPartyRewards(player: Player)
	{
		if (conf.party?.guaranteedRewards?.enabled == false)
		{
			return
		}
		
		val cmds = conf.party?.guaranteedRewards?.commands ?: return
		
		cmds.forEach()
		{ command ->
			server.dispatchCommand(server.consoleSender, formMessage(player, command))
		}
	}
	
	fun runPrePartyCommands()
	{
		if (conf.party?.prePartyCommands?.enabled == false)
		{
			return
		}
		
		val cmds = conf.party?.prePartyCommands?.commands ?: return
		
		cmds.forEach()
		{ command ->
			server.dispatchCommand(server.consoleSender, command)
		}
	}
	
	fun runPartyCommands()
	{
		if (conf.party?.partyCommands?.enabled == false)
		{
			return
		}
		
		val cmds = conf.party?.partyCommands?.commands ?: return
		
		cmds.forEach()
		{ command ->
			server.dispatchCommand(server.consoleSender, command)
		}
	}
	
	fun startParty()
	{
		runPrePartyCommands()
		
		server.scheduler.runTaskLater(plugin, Runnable {
			
			runPartyCommands()
			
			server.onlinePlayers.forEach()
			{
				giveGuaranteedPartyRewards(it)
				giveRandomPartyRewards(it)
			}
			
		}, (conf.party?.startDelay ?: 10) * 20)
	}
}
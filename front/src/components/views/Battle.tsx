import React from "react"
import { AdventureStateBattle, BattleActions, NextActions } from "../../models/Adventure"
import { executeAttackAction, executeSpellAction, executeLeaveAction, executeNextAction } from "../../api/api"
import { setExistingPlayer } from "../../models/Player"
import { useRecoilState } from "recoil"
import { adventureState, playerState } from "../../state/atoms"
import { makeSwitchExhaustive } from "../../utils"
import { Hero } from "../../models/Character"

interface BattleProps {
    adventureBattle: AdventureStateBattle
    hero: Hero
}

const Battle = ({ adventureBattle, hero }: BattleProps) => {
    const [selectedTarget, setSelectedTarget] = React.useState<number | null>(null)
    const [spellListOpen, setSpellListOpen] = React.useState(false)
    const [_player, setPlayer] = useRecoilState(playerState)
    const [_adventure, setAdventure] = useRecoilState(adventureState)

    React.useEffect(() => {
        if (selectedTarget !== null && !! adventureBattle.battle.enemies[selectedTarget]) {
            if (adventureBattle.battle.enemies[selectedTarget].hp <= 0) {
                setSelectedTarget(null)
            }
        }
    })

    const actionEnabled = selectedTarget !== null && !!adventureBattle.battle.enemies[selectedTarget]

    const onAttackAction = async () => {
        if (selectedTarget === null || !actionEnabled) {
            return
        }
        try {
            const adventure = await executeAttackAction(hero.name, selectedTarget)
            setAdventure(adventure)
        } catch (err) {
            console.log(err)
        }
    }

    const onSpellAction = async (spellName: string) => {
        try {
            const adventure = await executeSpellAction(hero.name, spellName, selectedTarget)
            setAdventure(adventure)
        } catch (err) {
            console.log(err)
        }
    }

    const onNextAction = async () => {
        if (adventureBattle.battle.state !== "Won") {
            return
        }
        try {
            const adventure = await executeNextAction(hero.name)
            setAdventure(adventure)
        } catch (err) {
            console.log(err)
        }
    }

    const onLeaveAction = async () => {
        if (adventureBattle.battle.state !== "Won") {
            return
        }
        try {
            const character = await executeLeaveAction(hero.name)
            setPlayer(setExistingPlayer(character))
            setAdventure(null)
        } catch (err) {
            console.log(err)
        }
    }

    const getEnemyList = () => {
        return adventureBattle.battle.enemies.map((e, i) => {
            return (
                <div onClick={() => setSelectedTarget(i)} >
                    <p>Enemy: {e.name}</p>
                    <p>Alive: {(e.hp > 0).toString()} </p>
                    <p>Targeted: {(i === selectedTarget).toString()}  </p>
                </div>
            )
        })
    }
    const getTitle = () => {
        switch(adventureBattle.battle.state) {
            case "Ongoing":
                return "Battle!"
            case "Won":
                return "Victory!"
            default:
                makeSwitchExhaustive(adventureBattle.battle.state)
        }
    }
    return (
        <div>
            <h2> {getTitle()}</h2>
            {getEnemyList()}
            {adventureBattle.battle.state === "Ongoing" &&
                <div>
                    <button disabled={!actionEnabled} onClick={onAttackAction}> Attack </button>
                    <button onClick={() => setSpellListOpen(!spellListOpen)}> Spells </button>
                    {spellListOpen && (
                        <div>
                            {hero.activeSpells.map(s => {
                                return <button key={s.name} onClick={() => onSpellAction(s.name)}> {s.name} </button>
                            })}
                        </div>
                    )}
                </div>
            }
            {adventureBattle.battle.state === "Won" &&
                <div>
                    <button onClick={onNextAction} > Next </button>
                    <button onClick={onLeaveAction}> Leave </button>
                </div>
            }
        </div>
    )
}

export default Battle
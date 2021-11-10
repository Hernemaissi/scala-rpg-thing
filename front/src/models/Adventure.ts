import * as t from 'io-ts'

import { pipe } from 'fp-ts/function'
import { fold } from 'fp-ts/Either'
import { ItemValidation } from './Item'

const EnemyValidator = t.type({
    name: t.string,
    maxHp: t.number,
    hp: t.number,
    damage: t.number,
    strength: t.number,
    intelligence: t.number,
    gold: t.number,
    exp: t.number
})

export type Enemy = t.TypeOf<typeof EnemyValidator>

const BattleValidator = t.type({
    state: t.union([t.literal("Ongoing"), t.literal("Won")]),
    enemies: t.array(EnemyValidator)
})

const AdventureValidator = t.type({
    name: t.string,
    possibleTroops: t.array(t.array(EnemyValidator)),
    possibleRewards: t.array(ItemValidation)
})

const AdventureStateBattleValidator = t.type({
    state: t.literal("Battle"),
    stage: t.number,
    adventure: AdventureValidator,
    battle: BattleValidator,
    bankedGold: t.number,
    bankedExperience: t.number
})

export type AdventureStateBattle = t.TypeOf<typeof AdventureStateBattleValidator>

const AdventureStateTreasureValidator = t.type({
    state: t.literal("Treasure"),
    stage: t.number,
    adventure: AdventureValidator,
    bankedGold: t.number,
    bankedExperience: t.number
})

export type AdventureStateTreasure = t.TypeOf<typeof AdventureStateTreasureValidator>

const AdventureStateValidator = t.union([AdventureStateBattleValidator, AdventureStateTreasureValidator])
export type AdventureState = AdventureStateBattle | AdventureStateTreasure

const decodeAdventureStateLeft = (errors: t.Errors) => {
    throw new Error(errors.map(error => error.context.map(({ key }) => key).join('.')).toString())
}

const decodeAdventureStateRight = (adventureState: AdventureState) => {
    return adventureState
}

export const decodeAdventure = (maybeAdventure: any): AdventureState => {
    return pipe(AdventureStateValidator.decode(maybeAdventure), fold(
        decodeAdventureStateLeft,
        decodeAdventureStateRight
    ))
}

export enum BattleActions {
    ATTACK = "ATTACK",
    SPELL = "SPELL"
}

export enum NextActions {
    FIGHT = "FIGHT",
    LEAVE = "LEAVE"
}
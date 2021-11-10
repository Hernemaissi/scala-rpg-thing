import * as t from 'io-ts'

import { pipe } from 'fp-ts/function'
import { fold } from 'fp-ts/Either'
import { GearValidation, InventoryItem, InventoryItemValidation, Item, ItemValidation } from './Item'
import { SpellValidator } from "./Spell"
import { decodeLeft, decodeRight } from './decode-utils'

const StatsValidator = t.type({
    maxHP: t.number,
    maxMana: t.number,
    strength: t.number,
    constitution: t.number,
    intelligence: t.number,
    mind: t.number
})

export type Stats = t.TypeOf<typeof StatsValidator>

export const HeroValidator = t.type({
    name: t.string,
    heroClass: t.string,
    exp: t.number,
    gold: t.number,
    level: t.number,
    stats: StatsValidator,
    maxActiveSpells: t.number,
    activeSpells: t.array(SpellValidator),
    knownSpells: t.array(SpellValidator),
    inventory: t.type({
        items: t.array(InventoryItemValidation),
    }),
    gear: GearValidation,
    location: t.type({
        name: t.union([t.literal("Town"), t.literal("Shop"), t.literal("Mage Tower")])
    })
})

export type Hero = t.TypeOf<typeof HeroValidator>

export const HeroSlotHeroValidator = t.type({
    id: t.number,
    name: t.string,
    heroClass: t.string,
    exp: t.number,
    gold: t.number,
    level: t.number,
    maxHP: t.number,
    maxMana: t.number,
    strength: t.number,
    intelligence: t.number,
    constitution: t.number,
    mind: t.number,
    maxActiveSpells: t.number
})

export const decodeHero = (maybeHero: any): Hero => {
    return pipe(HeroValidator.decode(maybeHero), fold(
        decodeLeft,
        decodeRight
    ))
}

export const HeroSlotValidator = t.type({
    id: t.number,
    owner: t.string,
    hero: t.union([HeroSlotHeroValidator, t.undefined])
})

export type HeroSlot = t.TypeOf<typeof HeroSlotValidator>

export const HeroSlotsValidator = t.array(HeroSlotValidator)

export type HeroSlots = t.TypeOf<typeof HeroSlotsValidator>

export const decodeHeroSlots = (maybeHeroSlot: any): HeroSlots => {
    return pipe(HeroSlotsValidator.decode(maybeHeroSlot), fold(
        decodeLeft,
        decodeRight
    ))
}

export const CharacterValidator = t.type({
    mode: t.string,
    hero: HeroSlotsValidator,
})

export type Character = t.TypeOf<typeof CharacterValidator>

export const decodeCharacter = (maybeCharacter: any): Character => {
    return pipe(CharacterValidator.decode(maybeCharacter), fold(
        decodeLeft,
        decodeRight
    ))
}

export const getGearName = (inventoryId: number | undefined, items: InventoryItem[]) => {
    if (inventoryId === undefined) {
        return "None"
    }
    const inventoryItem = items.find(i => i.inventoryId == inventoryId)
    if (!inventoryItem) {
        return "None"
    }
    return inventoryItem.item.name
}

export enum HeroClass {
    Warrior = "Warrior",
    Wizard = "Wizard",
    Spellsword = "Spellsword"
}

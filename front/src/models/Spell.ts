import * as t from "io-ts"

const TargetTypeValidator = t.union([t.literal("Single"), t.literal("All")])

const DamageEffectValidator = t.type({
    targetType: TargetTypeValidator,
    damage: t.number
})

const HealingEffectValidator = t.type({
    healing: t.number
})

export const SpellValidator = t.type({
    id: t.number,
    name: t.string,
    description: t.string,
    manaCost: t.number
})

export type Spell = t.TypeOf<typeof SpellValidator>
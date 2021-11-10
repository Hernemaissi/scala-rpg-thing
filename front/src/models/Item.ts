import * as t from 'io-ts'

export const ItemValidation = t.type({
    id: t.number,
    name: t.string,
    description: t.string
})

export type Item = t.TypeOf<typeof ItemValidation>

export const InventoryItemValidation = t.type({
    inventoryId: t.number,
    item: ItemValidation
})

export type InventoryItem = t.TypeOf<typeof InventoryItemValidation>

export type PurchasableItem = Item & { price: number }

export const GearValidation = t.type({
    weapon: t.union([t.number, t.undefined]),
    armor: t.union([t.number, t.undefined]),
    accessory: t.union([t.number, t.undefined])
})

export type Gear = t.TypeOf<typeof GearValidation>
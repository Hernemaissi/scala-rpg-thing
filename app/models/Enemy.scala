package models

import scala.collection.immutable.HashMap
import play.api.libs.json._
import monocle.macros.syntax.all._

case class Enemy(
    name: String,
    maxHp: Int,
    hp: Int,
    damage: Int,
    strength: Int,
    intelligence: Int,
    gold: Int,
    exp: Int
) {
    def takeDamage(amount: Int) = {
        this.focus(_.hp).replace(Math.max(0, hp - amount))
    }
}

object Enemy {
    implicit val json = Json.format[Enemy]
}

object EnemyUtils{
    val enemylistings = HashMap(
        "bat" -> Enemy(
            name = "Bat",
            maxHp = 7,
            hp = 7,
            damage = 3,
            strength = 1,
            intelligence = 1,
            gold = 2,
            exp = 5
        ),
        "bandit" -> Enemy(
            name = "Bandit",
            maxHp = 13,
            hp = 13,
            damage = 5,
            strength = 2,
            intelligence = 1,
            gold = 5,
            exp = 8
        )
    )

    val TwoBats = List(enemylistings("bat"), enemylistings("bat"))
    val Bandit = List(enemylistings("bandit"))
}
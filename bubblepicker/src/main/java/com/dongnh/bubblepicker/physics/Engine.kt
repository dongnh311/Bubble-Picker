package com.dongnh.bubblepicker.physics

import com.dongnh.bubblepicker.rendering.Item
import com.dongnh.bubblepicker.sqr
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.World
import java.util.*
import kotlin.math.abs

/**
 * Created by irinagalata on 1/26/17.
 */
object Engine {
    var isAlwaysSelected = true

    val selectedBodies: List<CircleBody>
        get() = bodies.filter { it.increased || it.toBeIncreased || it.isIncreasing }

    var maxSelectedCount: Int? = null
    var radius = 50
        set(value) {
            bubbleRadius = interpolate(0.1f, 0.25f, value / 100f)
            speedToCenter = interpolate(20f, 80f, value / 100f)
            standardIncreasedGravity = interpolate(500f, 800f, value / 100f)
            field = value
        }
    var centerImmediately = false
    private var standardIncreasedGravity = interpolate(500f, 800f, 0.5f)
    private var bubbleRadius = 0.17f

    private var world = World(Vec2(0f, 0f), false)
    private val step = 0.0009f
    private val bodies: ArrayList<CircleBody> = ArrayList()
    private var borders: ArrayList<Border> = ArrayList()
    private val resizeStep = 0.009f
    private var scaleX = 0f
    private var scaleY = 0f
    private var touch = false
    var speedToCenter = 16f
    private var increasedGravity = 55f
    private var gravityCenter = Vec2(0f, 0f)
    private val currentGravity: Float
        get() = if (touch) increasedGravity else speedToCenter
    private val toBeResized = ArrayList<Item>()
    private val startX
        get() = if (centerImmediately) 0.5f else 2.2f
    private var stepsCount = 0
    var marginItem = 0.001f

    fun build(bodiesCount: Int, scaleX: Float, scaleY: Float): List<CircleBody> {
        val density = interpolate(0.8f, 0.2f, radius / 100f)
        for (i in 0 until bodiesCount) {
            val x = if (Random().nextBoolean()) -startX else startX
            val y = if (Random().nextBoolean()) -0.5f / scaleY else 0.5f / scaleY
            bodies.add(
                CircleBody(
                    world, Vec2(x, y),
                    bubbleRadius * scaleX,
                    (bubbleRadius * scaleX) * 1.3f,
                    density = density,
                    isAlwaysSelected = isAlwaysSelected,
                    marinItem = marginItem
                )
            )
        }
        Engine.scaleX = scaleX
        Engine.scaleY = scaleY
        createBorders()

        return bodies
    }

    fun move() {
        toBeResized.forEach { it.circleBody.resize(resizeStep) }
        world.step(if (centerImmediately) 0.035f else step, 11, 11)
        bodies.forEach { move(it) }
        toBeResized.removeAll(toBeResized.filter { it.circleBody.finished }.toSet())
        stepsCount++
        if (stepsCount >= 10) {
            centerImmediately = false
        }
    }

    fun swipe(x: Float, y: Float) {
        if (abs(gravityCenter.x) < 2) gravityCenter.x += -x
        if (abs(gravityCenter.y) < 0.5f / scaleY) gravityCenter.y += y
        increasedGravity = standardIncreasedGravity * abs(x * 13) * abs(y * 13)
        touch = true
    }

    fun release() {
        gravityCenter.setZero()
        touch = false
        increasedGravity = standardIncreasedGravity
    }

    fun clear() {
        borders.forEach { world.destroyBody(it.itemBody) }
        bodies.forEach { world.destroyBody(it.physicalBody) }
        world = World(Vec2(0f, 0f), false)
        borders.clear()
        bodies.clear()
    }

    fun resize(item: Item): Boolean {
        if (selectedBodies.size >= (maxSelectedCount
                ?: bodies.size) && !item.circleBody.increased
        ) return false

        if (item.circleBody.isBusy) return false

        item.circleBody.defineState()

        toBeResized.add(item)

        return true
    }

    private fun createBorders() {
        borders = arrayListOf(
            Border(world, Vec2(0f, 0.5f / scaleY), Border.HORIZONTAL),
            Border(world, Vec2(0f, -0.5f / scaleY), Border.HORIZONTAL)
        )
    }

    private fun move(body: CircleBody) {
        body.physicalBody.apply {
            body.isVisible = centerImmediately.not()
            val direction = gravityCenter.sub(position)
            val distance = direction.length()
            val gravity = if (body.increased) 1.3f * currentGravity else currentGravity
            if (distance > step * 200) {
                applyForce(direction.mul(gravity * 5 / distance.sqr()), position)
            }
        }
    }

    private fun interpolate(start: Float, end: Float, f: Float) = start + f * (end - start)

}
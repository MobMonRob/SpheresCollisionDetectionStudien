/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.visualizer.object;

import org.jogamp.vecmath.Vector4f;

public record Material(
        Vector4f ambient,
        Vector4f diffuse,
        Vector4f specular,
        float alpha
) {
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pagosoft.colormixer;

import com.pagosoft.swing.BaseColor;
import java.awt.Color;

/**
 *
 * @author pago
 */
public class AnalogousColorCalculator implements ColorCalculator {
    public BaseColor[] calculateColors(BaseColor color) {
        BaseColor split1 = color.shiftHue(30);
        BaseColor split2 = color.shiftHue(-30);
        BaseColor result[] = new BaseColor[] {
            color, color.darker(1), color.darker(2),
            split1, split1.darker(1), split1.darker(2),
            split2, split2.darker(1), split2.darker(2)
        };
        return result;
    }
}

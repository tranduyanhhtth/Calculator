package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {
    private lateinit var textViewResult: TextView
    private var currentInput: String = ""
    private var result: BigDecimal = BigDecimal.ZERO
    private var lastOperation: String = "="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewResult = findViewById(R.id.textViewResult)

        val numberButtons = arrayOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
        )
        numberButtons.forEach { buttonId ->
            findViewById<Button>(buttonId).setOnClickListener { appendNumber(buttonId) }
        }

        findViewById<Button>(R.id.btnAdd).setOnClickListener { handleOperation("+") }
        findViewById<Button>(R.id.btnSubtract).setOnClickListener { handleOperation("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { handleOperation("x") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { handleOperation("/") }
        findViewById<Button>(R.id.btnEquals).setOnClickListener { calculateResult() }
        findViewById<Button>(R.id.btnCE).setOnClickListener { clearEntry() }
        findViewById<Button>(R.id.btnC).setOnClickListener { clearAll() }
        findViewById<Button>(R.id.btnBS).setOnClickListener { backspace() }
        findViewById<Button>(R.id.btnPlusMinus).setOnClickListener { toggleSign() }
    }

    private fun appendNumber(buttonId: Int) {
        val value = when (buttonId) {
            R.id.btn0 -> "0"
            R.id.btn1 -> "1"
            R.id.btn2 -> "2"
            R.id.btn3 -> "3"
            R.id.btn4 -> "4"
            R.id.btn5 -> "5"
            R.id.btn6 -> "6"
            R.id.btn7 -> "7"
            R.id.btn8 -> "8"
            R.id.btn9 -> "9"
            R.id.btnDot -> if (!currentInput.contains(".")) "." else return
            else -> return
        }
        currentInput += value
        textViewResult.text = currentInput
    }

    private fun handleOperation(operation: String) {
        if (currentInput.isNotEmpty()) {
            val inputNumber = currentInput.toBigDecimalOrNull()
            if (inputNumber == null) {
                textViewResult.text = "Error"
                currentInput = ""
                return
            }

            if (lastOperation != "=") {
                when (lastOperation) {
                    "+" -> result = result.add(inputNumber)
                    "-" -> result = result.subtract(inputNumber)
                    "x" -> result = result.multiply(inputNumber)
                    "/" -> {
                        if (inputNumber == BigDecimal.ZERO) {
                            textViewResult.text = "Error"
                            clearAll()
                            return
                        } else {
                            try {
                                result = result.divide(inputNumber, 10, RoundingMode.HALF_UP)
                            } catch (e: ArithmeticException) {
                                textViewResult.text = "Error"
                                clearAll()
                                return
                            }
                        }
                    }
                }
                val displayResult = result.stripTrailingZeros()
                textViewResult.text = if (displayResult.remainder(BigDecimal.ONE) == BigDecimal.ZERO) displayResult.toLong().toString() else displayResult.toString()
            } else {
                result = inputNumber
            }

            lastOperation = operation
            currentInput = ""
        } else {
            lastOperation = operation
        }
    }

    private fun calculateResult() {
        if (currentInput.isEmpty()) return

        val inputNumber = currentInput.toBigDecimalOrNull()
        if (inputNumber == null) {
            textViewResult.text = "Error"
            currentInput = ""
            return
        }

        when (lastOperation) {
            "+" -> result = result.add(inputNumber)
            "-" -> result = result.subtract(inputNumber)
            "x" -> result = result.multiply(inputNumber)
            "/" -> {
                if (inputNumber == BigDecimal.ZERO) {
                    textViewResult.text = "Error"
                    clearAll()
                    return
                } else {
                    try {
                        result = result.divide(inputNumber, 10, RoundingMode.HALF_UP)
                    } catch (e: ArithmeticException) {
                        textViewResult.text = "Error"
                        clearAll()
                        return
                    }
                }
            }
            "=" -> result = inputNumber
        }

        currentInput = ""
        lastOperation = "="
        val displayResult = result.stripTrailingZeros()
        textViewResult.text = if (displayResult.remainder(BigDecimal.ONE) == BigDecimal.ZERO) displayResult.toLong().toString() else displayResult.toString()
    }

    private fun clearEntry() {
        currentInput = ""
        textViewResult.text = "0"
    }

    private fun clearAll() {
        currentInput = ""
        result = BigDecimal.ZERO
        lastOperation = "="
        textViewResult.text = "0"
    }

    private fun backspace() {
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            textViewResult.text = if (currentInput.isEmpty()) "0" else currentInput
        }
    }

    private fun toggleSign() {
        if (currentInput.isNotEmpty()) {
            val value = currentInput.toBigDecimalOrNull() ?: return
            currentInput = value.negate().toString()
            textViewResult.text = currentInput
        }
    }
}
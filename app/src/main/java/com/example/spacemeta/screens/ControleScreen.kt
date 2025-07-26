package com.example.spacemeta.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.spacemeta.ui.theme.DataStoreManager
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@Composable
fun ControleAScreen(periodo: String) {
    val context = LocalContext.current
    val db = Firebase.firestore
    val dataStore = remember { DataStoreManager(context) }

    val scope = rememberCoroutineScope()

    val produtos = listOf(
        "PROFISSIONALIZANTE",
        "TÉCNICO",
        "GRADUAÇÃO",
        "HÍBRIDO-BIOFAR",
        "HÍBRIDO-BEM-ESTAR",
        "HÍBRIDO-NUTRIÇÃO",
        "PÓS-GRADUAÇÃO"
    ) //Criando uma lista imutável de produtos
    val metas = remember {
        produtos.associateWith { mutableStateOf(" ") }
    }
    val realizado = remember {
        produtos.associateWith { mutableStateOf(" ") }
    }
    val scrollState = rememberScrollState()
    LaunchedEffect(Unit) {
        produtos.forEach { produto ->
            val chaveMeta = "meta_${produto}_$periodo"
            val chaveRealizado = "realizado_${produto}_$periodo"
            launch {
                dataStore.getValor(chaveMeta).collect { valor ->
                    metas[produto]?.value = valor
                }
            }
            launch {
                dataStore.getValor(chaveRealizado).collect { valor ->
                    realizado[produto]?.value = valor
                }
            }
        }
    }
    //Calcula o percentual de metas atingidas
    val percentualTotal = remember {
        derivedStateOf {
            val metasDouble = produtos.mapNotNull { metas[it]?.value?.toDoubleOrNull() }
            val realizadoDouble = produtos.mapNotNull { realizado[it]?.value?.toDoubleOrNull() }
            if (metasDouble.sum() > 0) {
                (realizadoDouble.sum() / metasDouble.sum()) * 100
            } else 0.0
        }
    }
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
            .navigationBarsPadding()
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "MATRÍCULAS - $periodo",
            style = MaterialTheme.typography.headlineSmall
        )

        produtos.forEach { produto ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {

                    // Título + Lixeira no topo do Card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = produto,
                            style = MaterialTheme.typography.titleMedium
                        )
                        IconButton(onClick = {
                            metas[produto]?.value = ""
                            realizado[produto]?.value = ""

                            val chaveMeta = "meta_${produto}_$periodo"
                            val chaveRealizado = "realizado_${produto}_$periodo"
                            val docRef = db.collection("metas").document(periodo)

                            scope.launch {
                                dataStore.salvarValor(chaveMeta, "")
                                dataStore.salvarValor(chaveRealizado, "")

                                val updates = mapOf(
                                    "${produto}_meta" to "",
                                    "${produto}_realizado" to ""
                                )
                                docRef.update(updates).addOnFailureListener {
                                    docRef.set(updates, SetOptions.merge())
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Limpar dados",
                                tint = Color(0xFFFF908E)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = metas[produto]?.value ?: "",
                            onValueChange = { input ->
                                metas[produto]?.value = input

                                val chaveMeta = "meta_${produto}_$periodo"
                                val docRef = db.collection("metas").document(periodo)

                                scope.launch {
                                    dataStore.salvarValor(chaveMeta, input)
                                    docRef.update("${produto}_meta", input)
                                        .addOnFailureListener {
                                            docRef.set(
                                                mapOf("${produto}_meta" to input),
                                                SetOptions.merge()
                                            )
                                        }
                                }
                            },
                            label = { Text("Meta") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )

                        OutlinedTextField(
                            value = realizado[produto]?.value ?: "",
                            onValueChange = { input ->
                                realizado[produto]?.value = input

                                val chaveRealizado = "realizado_${produto}_$periodo"
                                val docRef = db.collection("metas").document(periodo)

                                scope.launch {
                                    dataStore.salvarValor(chaveRealizado, input)
                                    docRef.update("${produto}_realizado", input)
                                        .addOnFailureListener {
                                            docRef.set(
                                                mapOf("${produto}_realizado" to input),
                                                SetOptions.merge()
                                            )
                                        }
                                }
                            },
                            label = { Text("Realizado") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        val corMeta = when {
            percentualTotal.value >= 100 -> Color(0xFF279AF1)
            percentualTotal.value >= 80 -> Color(0xFF00a878)
            percentualTotal.value >= 70 -> Color(0xFFF6F740)
            else -> Color(0xFFFE5E41)
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = corMeta),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "META ATINGIDA: ${"%.1f".format(percentualTotal.value)}%",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.navigationBarsPadding())
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
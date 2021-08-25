package br.com.zup

import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class CarrosEndpointTest(val repository : CarroRepository, val grpcClient : CarrosGrpcServiceGrpc.CarrosGrpcServiceBlockingStub) {

    /**
     * 1. happy path - ok
     * 2. quando ja existe o carro com a placa
     * 3. quando os dados de entrada sao invalidos
     */

    @Test
    fun `deve adicionar um novo carro`() {
        //cenario
        repository.deleteAll()

        //acao
        val response = grpcClient.adicionar(CarroRequest.newBuilder()
                                                        .setModelo("Gol")
                                                        .setPlaca("HPX-1234")
                                                        .build())

        //validacao
        with(response) {
            assertNotNull(id)
            assertTrue(repository.existsById(id)) //efeito colateral(insert no banco)
        }

    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): CarrosGrpcServiceGrpc.CarrosGrpcServiceBlockingStub {
            return CarrosGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}
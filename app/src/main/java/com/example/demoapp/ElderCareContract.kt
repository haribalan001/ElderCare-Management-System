package com.example.demoapp

import io.reactivex.Flowable
import org.web3j.abi.EventEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Event
import org.web3j.abi.datatypes.Utf8String
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.RemoteCall
import org.web3j.protocol.core.methods.request.EthFilter
import org.web3j.protocol.core.methods.response.Log
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.tx.Contract
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider
import java.math.BigInteger
import java.util.*

class ElderCareContract : Contract {
    companion object {
        private const val BINARY = "608060405234801561000f575f5ffd5b5060405161071d38038061071d83398101604081905261002e91610057565b5f610039838261018f565b5060015550610249565b634e487b7160e01b5f52604160045260245ffd5b5f5f60408385031215610068575f5ffd5b82516001600160401b0381111561007d575f5ffd5b8301601f8101851361008d575f5ffd5b80516001600160401b038111156100a6576100a6610043565b604051601f8201601f19908116603f011681016001600160401b03811182821017156100d4576100d4610043565b6040528181528282016020018710156100eb575f5ffd5b8160208401602083015e5f60209282018301529401519395939450505050565b600181811c9082168061011f57607f821691505b60208210810361013d57634e487b7160e01b5f52602260045260245ffd5b50919050565b601f82111561018a57805f5260205f20601f840160051c810160208510156101685750805b601f840160051c820191505b81811015610187575f8155600101610174565b50505b505050565b81516001600160401b038111156101a8576101a8610043565b6101bc816101b6845461010b565b84610143565b6020601f8211600181146101ee575f83156101d75750848201515b5f19600385901b1c1916600184901b178455610187565b5f84815260208120601f198516915b8281101561021d57878501518255602094850194600190920191016101fd565b508482101561023a57868401515f19600387901b60f8161c191681555b50505050600190811b01905550565b6104c7806102565f395ff3fe608060405234801561000f575f5ffd5b506004361061004a575f3560e01c80633b142a571461004e5780637902413b1461006c578063bc7fb65214610083578063be63192714610098575b5f5ffd5b6100566100ae565b604051610063919061024f565b60405180910390f35b61007560015481565b604051908152602001610063565b61009661009136600461027c565b610139565b005b6100a0610188565b604051610063929190610331565b5f80546100ba90610352565b80601f01602080910402602001604051908101604052809291908181526020018280546100e690610352565b80156101315780601f1061010857610100808354040283529160200191610131565b820191905f5260205f20905b81548152906001019060200180831161011457829003601f168201915b505050505081565b5f61014483826103d6565b5060018190556040517f9a57d2a9552a8a8833647034c966fc55157bd9eeb045fa11709baf17f24bd98e9061017c9084908490610331565b60405180910390a15050565b60605f5f60015481805461019b90610352565b80601f01602080910402602001604051908101604052809291908181526020018280546101c790610352565b80156102125780601f106101e957610100808354040283529160200191610212565b820191905f5260205f20905b8154815290600101906020018083116101f557829003601f168201915b50505050509150915091509091565b5f81518084528060208401602086015e5f602082860101526020601f19601f83011685010191505092915050565b602081525f6102616020830184610221565b9392505050565b634e487b7160e01b5f52604160045260245ffd5b5f5f6040838503121561028d575f5ffd5b823567ffffffffffffffff8111156102a3575f5ffd5b8301601f810185136102b3575f5ffd5b803567ffffffffffffffff8111156102cd576102cd610268565b604051601f8201601f19908116603f0116810167ffffffffffffffff811182821017156102fc576102fc610268565b604052818152828201602001871015610313575f5ffd5b816020840160208301375f6020928201830152969401359450505050565b604081525f6103436040830185610221565b90508260208301529392505050565b600181811c9082168061036657607f821691505b60208210810361038457634e487b7160e01b5f52602260045260245ffd5b50919050565b601f8211156103d157805f5260205f20601f840160051c810160208510156103af5750805b601f840160051c820191505b818110156103ce575f81556001016103bb565b50505b505050565b815167ffffffffffffffff8111156103f0576103f0610268565b610404816103fe8454610352565b8461038a565b6020601f821160018114610436575f831561041f5750848201515b5f19600385901b1c1916600184901b1784556103ce565b5f84815260208120601f198516915b828110156104655787850151825560209485019460019092019101610445565b508482101561048257868401515f19600387901b60f8161c191681555b50505050600190811b0190555056fea2646970667358221220fec567cb9a07c84e60c5792f20389877a13999d995a76b31175787a11c1f5e7c64736f6c634300081c0033"

        const val FUNC_ELDERAGE = "elderAge"
        const val FUNC_ELDERNAME = "elderName"
        const val FUNC_GETELDERDETAILS = "getElderDetails"
        const val FUNC_SETELDERDETAILS = "setElderDetails"

        val ELDERADDED_EVENT = Event(
            "ElderAdded",
            listOf(TypeReference.create(Utf8String::class.java), TypeReference.create(Uint256::class.java))
        )

        fun deploy(
            web3j: Web3j,
            credentials: Credentials,
            contractGasProvider: ContractGasProvider
        ): RemoteCall<ElderCareContract> {
            return deployRemoteCall(
                ElderCareContract::class.java, web3j, credentials, contractGasProvider, BINARY, ""
            )
        }

        fun load(
            contractAddress: String, web3j: Web3j, credentials: Credentials, contractGasProvider: ContractGasProvider
        ) = ElderCareContract(contractAddress, web3j, credentials, contractGasProvider)
    }

    constructor(
        contractAddress: String,
        web3j: Web3j,
        credentials: Credentials,
        contractGasProvider: ContractGasProvider
    ) : super(BINARY, contractAddress, web3j, credentials, contractGasProvider)

    constructor(
        contractAddress: String,
        web3j: Web3j,
        transactionManager: TransactionManager,
        contractGasProvider: ContractGasProvider
    ) : super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider)

    fun getElderAddedEvents(transactionReceipt: TransactionReceipt): List<ElderAddedEventResponse> {
        return extractEventParametersWithLog(ELDERADDED_EVENT, transactionReceipt).map {
            ElderAddedEventResponse(it.log, it.nonIndexedValues[0].value as String, it.nonIndexedValues[1].value as BigInteger)
        }
    }

    fun elderAddedEventFlowable(filter: EthFilter): Flowable<ElderAddedEventResponse> {
        return web3j.ethLogFlowable(filter).map {
            val eventValues = extractEventParametersWithLog(ELDERADDED_EVENT, it)
            ElderAddedEventResponse(it, eventValues.nonIndexedValues[0].value as String, eventValues.nonIndexedValues[1].value as BigInteger)
        }
    }

    fun elderAddedEventFlowable(startBlock: DefaultBlockParameter, endBlock: DefaultBlockParameter): Flowable<ElderAddedEventResponse> {
        val filter = EthFilter(startBlock, endBlock, contractAddress).apply {
            addSingleTopic(EventEncoder.encode(ELDERADDED_EVENT))
        }
        return elderAddedEventFlowable(filter)
    }

    fun elderAge(): RemoteCall<TransactionReceipt> = executeRemoteCallTransaction(
        org.web3j.abi.datatypes.Function(FUNC_ELDERAGE, emptyList(), emptyList())
    )

    fun elderName(): RemoteCall<TransactionReceipt> = executeRemoteCallTransaction(
        org.web3j.abi.datatypes.Function(FUNC_ELDERNAME, emptyList(), emptyList())
    )

    fun getElderDetails(): RemoteCall<TransactionReceipt> = executeRemoteCallTransaction(
        org.web3j.abi.datatypes.Function(FUNC_GETELDERDETAILS, emptyList(), emptyList())
    )

    fun setElderDetails(name: String, age: BigInteger): RemoteCall<TransactionReceipt> = executeRemoteCallTransaction(
        org.web3j.abi.datatypes.Function(
            FUNC_SETELDERDETAILS,
            listOf(Utf8String(name), Uint256(age)),
            emptyList()
        )
    )

    data class ElderAddedEventResponse(
        val log: Log,
        val name: String,
        val age: BigInteger
    )
}

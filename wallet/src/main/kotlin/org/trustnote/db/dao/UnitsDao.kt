package org.trustnote.db.dao

import android.arch.persistence.room.*
import io.reactivex.Flowable
import io.reactivex.Single
import org.trustnote.db.*
import org.trustnote.db.entity.*
import org.trustnote.superwallet.biz.TTT
import org.trustnote.superwallet.util.Utils

@SuppressWarnings("unchecked")
@Suppress("UNCHECKED_CAST")
@Dao
abstract class UnitsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertUnits(units: Array<Units>)

    @Query("SELECT * FROM units")
    abstract fun queryUnits(): Array<Units>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertInputs(inputs: Array<Inputs>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertOutputs(inputs: Array<Outputs>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMessages(outputs: Array<Messages>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAuthentifiers(outputs: Array<Authentifiers>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertDefinitions(outputs: Array<Definitions>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCorrespondentDevice(correspondentDevices: CorrespondentDevices)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertChatMessages(chatMessages: ChatMessages)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertOutbox(outbox: Outbox)

    @Delete
    abstract fun deleteOutbox(outbox: Outbox)

        @Query("""
        SELECT unit FROM inputs WHERE inputs.address IN (SELECT my_addresses.address
        FROM my_addresses WHERE my_addresses.wallet == :walletId
        ORDER BY my_addresses.address_index DESC LIMIT :dataLimit)
        UNION
        SELECT unit from outputs WHERE outputs.address in (SELECT my_addresses.address
        FROM my_addresses WHERE my_addresses.wallet == :walletId and my_addresses.is_change = :isChange
        ORDER BY my_addresses.address_index DESC LIMIT :dataLimit)
        """)
    abstract fun queryUnitForLatestWalletAddress(walletId: String, isChange: Int, dataLimit: Int = TTT.walletAddressInitSize): Array<String>

    @Query("""select count(*) from definitions where definition_chash = :address""")
    abstract fun findDefinitions(address: String): Int

    @Query("""SELECT * FROM units""")
    abstract fun monitorUnits(): Flowable<Array<Units>>

    @Query("""SELECT * FROM outbox ORDER BY creation_date DESC limit 1""")
    abstract fun monitorOutbox(): Flowable<Array<Outbox>>

    @Query("""SELECT * FROM outputs""")
    abstract fun monitorOutputs(): Flowable<Array<Outputs>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMyAddresses(myAddresses: Array<MyAddresses>)

    @Query("""SELECT * FROM my_addresses WHERE wallet == :walletId""")
    abstract fun queryAllWalletAddress(walletId: String): Array<MyAddresses>

    @Query("""
        SELECT max(address_index) FROM my_addresses WHERE wallet == :walletId and is_change = :isChange
        """)
    abstract fun getMaxAddressIndex(walletId: String, isChange: Int): Int

    @Query("SELECT * FROM my_addresses")
    abstract fun queryAllWalletAddress(): Array<MyAddresses>

    @Query("SELECT * FROM my_addresses WHERE address in (:addressList)")
    abstract fun queryAddress(addressList: List<String>): Array<MyAddresses>

    @Query("SELECT * FROM my_addresses WHERE wallet = :walletId")
    abstract fun queryAddressByWalletId(walletId: String): Array<MyAddresses>

    @Query("SELECT * FROM my_addresses")
    abstract fun monitorAddresses(): Flowable<Array<MyAddresses>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMyWitnesses(myWitnesses: Array<MyWitnesses>)

    @Delete
    abstract fun deleteMyWitnesses(myWitnesses: Array<MyWitnesses>)

    @Query("DELETE FROM chat_messages WHERE correspondent_address = :correspondentAddresses")
    abstract fun clearChatHistory(correspondentAddresses: String)

    @Delete
    abstract fun removeCorrespondentDevice(correspondentDevice: CorrespondentDevices)

    @Query("SELECT * FROM my_witnesses")
    abstract fun queryMyWitnesses(): Array<MyWitnesses>

    @Query("SELECT * FROM correspondent_devices where device_address = :correspondentDevices")
    abstract fun findCorrespondentDevices(correspondentDevices: String): Array<CorrespondentDevices>

    @Query("SELECT * FROM correspondent_devices where pubkey = :ppubkey")
    abstract fun findCorrespondentDeviceByPubkey(ppubkey: String): Array<CorrespondentDevices>

    @Query("SELECT * FROM correspondent_devices ORDER BY last_message_creation_date DESC")
    abstract fun queryCorrespondetnDevices(): Array<CorrespondentDevices>

    @Query("SELECT * FROM chat_messages where correspondent_address = :correspondentAddress")
    abstract fun queryChatMessages(correspondentAddress: String): Array<ChatMessages>

    @Query("""
        UPDATE correspondent_devices SET unread_counter =
        (SELECT count(*) FROM chat_messages
            WHERE correspondent_address = :correspondentAddress and chat_messages.is_read = 0)
        WHERE device_address = :correspondentAddress
        """)
    abstract fun updateUnreadMessageCounter(correspondentAddress: String): Int

    @Query("""
        UPDATE chat_messages SET is_read = 1
        WHERE correspondent_address = :correspondentAddress
        """)
    abstract fun readAllMessages(correspondentAddress: String): Int

    @Query("""
        UPDATE correspondent_devices SET last_message = :lastMessage, last_message_creation_date = :lastMessageCreationDate
        WHERE device_address = :correspondentAddress
        """)
    abstract fun updateLastMessage(correspondentAddress: String, lastMessage: String, lastMessageCreationDate: Long): Int

    @Query("""
        SELECT outputs.address, COALESCE(outputs.asset, 'base') as asset, sum(outputs.amount) as amount
        FROM outputs, my_addresses
        WHERE outputs.address = my_addresses.address and outputs.asset IS NULL
        AND my_addresses.wallet = :walletId
        AND outputs.is_spent=0
        GROUP BY outputs.address
        ORDER BY my_addresses.address_index ASC
        """)
    abstract fun queryBalance(walletId: String): Array<Balance>

    @Query("""SELECT outputs.*
        FROM outputs JOIN inputs ON outputs.unit=inputs.src_unit
        AND outputs.message_index=inputs.src_message_index
        AND outputs.output_index=inputs.src_output_index
        WHERE is_spent=0 and outputs.asset IS NULL and inputs.asset IS NULL
        """)
    abstract fun querySpentOutputs(): Array<Outputs>

    @Query("""
        UPDATE outputs SET is_spent=1
        WHERE unit = :unitId
        AND message_index= :messageIndex
        AND output_index = :outputIndex
        """)
    abstract fun fixIsSpentFlag(unitId: String, messageIndex: Int, outputIndex: Int): Int

    @Query("""
        UPDATE units SET is_stable=1, is_free=0 WHERE unit IN( :unitIds) """)
    abstract fun unitsStabled(unitIds: Array<String>): Int

    @Query("""SELECT unit, level, is_stable, sequence, address, units.creation_date as ts,
        headers_commission+payload_commission AS fee,
        SUM(amount) AS amount, address AS to_address, '' AS from_address, main_chain_index AS mci
        FROM units
        JOIN outputs USING(unit)
        JOIN my_addresses USING(address)
        WHERE wallet= :walletId and asset IS NULL
        GROUP BY unit, address
        UNION
        SELECT unit, level, is_stable, sequence, address, units.creation_date as ts,
        headers_commission+payload_commission AS fee,
        NULL AS amount, '' AS to_address, address AS from_address, main_chain_index AS mci
        FROM units
        JOIN inputs USING(unit)
        JOIN my_addresses USING(address)
        WHERE wallet= :walletId and asset IS NULL
        ORDER BY ts DESC
        """)
    abstract fun queryTxUnits(walletId: String): Array<TxUnits>

    @Query("SELECT DISTINCT address FROM inputs WHERE unit= :unitId ORDER BY address")
    abstract fun queryInputAddresses(unitId: String): Array<String>

    @Query("""
        SELECT my_addresses.* FROM my_addresses
        LEFT JOIN outputs ON outputs.address = my_addresses.address
        WHERE outputs.address IS NULL
        AND my_addresses.wallet = :walletId
        AND is_change = 1
        LIMIT 1
        """)
    abstract fun queryUnusedChangeAddress(walletId: String): Array<MyAddresses>

    @Query("""
        SELECT outputs.address, SUM(amount) AS amount, (my_addresses.address IS NULL) AS is_external
        FROM outputs LEFT JOIN my_addresses ON outputs.address=my_addresses.address
        AND wallet= :walletId
        WHERE unit= :unitId
        GROUP BY outputs.address
        """)
    abstract fun queryOutputAddress(unitId: String, walletId: String): Array<TxOutputs>

    @Query("""
        SELECT address, SUM(amount) AS total
        FROM outputs JOIN my_addresses USING(address)
        CROSS JOIN units USING(unit)
        WHERE wallet = :walletId
        AND is_stable = 1
        AND sequence = 'good'
        AND is_spent = 0
        AND asset IS NULL
        GROUP BY address ORDER BY SUM(amount) > :estimateAmount DESC, ABS(SUM(amount) - :estimateAmount) ASC;
        """)
    //    TODO: unclear logic.
    //    AND NOT EXISTS (
    //    SELECT * FROM unit_authors JOIN units USING(unit)
    //    WHERE is_stable=0 AND unit_authors.address=outputs.address AND definition_chash IS NOT NULL)
    abstract fun queryFundedAddressesByAmount(walletId: String, estimateAmount: Long): Array<FundedAddress>

    @Query("""
        SELECT unit, message_index, output_index, amount, address, blinding, is_spent, denomination
        FROM outputs
        CROSS JOIN units USING(unit)
        WHERE address IN( :addressList)
        AND asset IS NULL
        AND is_spent=0
        AND is_stable=1
        AND sequence='good'
        AND main_chain_index <= :lastBallMCI
        ORDER BY amount DESC
        """)
    abstract fun queryUtxoByAddress(addressList: List<String>, lastBallMCI: Int): Array<Outputs>

    @Transaction
    open fun saveMyWitnesses(myWitnesses: Array<MyWitnesses>) {
        val oldWitnesses = queryMyWitnesses()
        deleteMyWitnesses(oldWitnesses)
        insertMyWitnesses(myWitnesses)
    }

    @Transaction
    open fun saveUnits(units: Array<Units>) {
        insertUnits(units)
        for (oneUnit in units) {
            insertDefinitions(oneUnit.definitions.toTypedArray())
            insertAuthentifiers(oneUnit.authenfiers.toTypedArray())
            insertMessages(oneUnit.messages.toTypedArray())
            for (oneMessage in oneUnit.messages) {
                insertInputs(oneMessage.payload.inputs.toTypedArray())
                insertOutputs(oneMessage.payload.outputs.toTypedArray())
            }
        }
    }

    @Transaction
    open fun getUnitxByWalletId() {
        val units = queryUnits()
        for (oneUnit in units) {

            insertMessages(oneUnit.messages.toTypedArray())
            for (oneMessage in oneUnit.messages) {
                insertInputs(oneMessage.payload.inputs.toTypedArray())
                insertOutputs(oneMessage.payload.outputs.toTypedArray())
            }
        }
    }

    open fun shouldGenerateMoreAddress(walletId: String, isChange: Int): Boolean {
        val res = queryUnitForLatestWalletAddress(walletId, isChange)
        return res.isNotEmpty()
    }

    open fun shouldGenerateNextWallet(walletId: String): Boolean {
        val res = queryUnitForLatestWalletAddress(walletId, Int.MAX_VALUE)
        return res.isNotEmpty()
    }

    open fun fixIsSpentFlag() {
        querySpentOutputs().forEach {
            fixIsSpentFlag(it.unit, it.messageIndex, it.outputIndex)
        }
    }

}
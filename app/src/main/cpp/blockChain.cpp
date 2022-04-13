
#include "blockChain.h"

namespace howl {

    BlockChain::BlockChain(char* chatId) {

        _chatId = (char*) malloc(sizeof(char) * (SHA512_HEX_DIGEST_LENGTH + 1));
        sprintf(_chatId, "%s", chatId);
        _sentLength = 0;
        _receivedLength = 0;
        _work = 3;

        _sentHead = NULL;
        _receivedHead = NULL;
    }

    void BlockChain::buildGenisisBlock() {

        Block*  genBlock;
        char*   initialPreviousHash;
        char*   initialCurrentHash;
        char*   initialMerklerootHash;
        char*   initialMessage;

        initialPreviousHash = (char*) malloc(sizeof(char) * 5);
        initialCurrentHash = (char*) malloc(sizeof(char) * 5);
        initialMerklerootHash = (char*) malloc(sizeof(char) * 5);
        initialMessage = (char*) malloc(sizeof(char) * 13);

        sprintf(initialPreviousHash, "NULL");
        sprintf(initialCurrentHash, "NULL");
        sprintf(initialMerklerootHash, "NULL");
        sprintf(initialMessage, "GENESISBLOCK");

        genBlock = new Block(
                _sentLength++,
                nullptr,
                initialPreviousHash,
                initialCurrentHash,
                initialMerklerootHash,
                initialMessage);

        // TODO, could be messing causing memory issues.
        (*genBlock).mine(_work);
        _sentHead = genBlock;

        free(initialPreviousHash);
        free(initialCurrentHash);
        free(initialMerklerootHash);
        free(initialMessage);
    }

    void BlockChain::buildSentBlock(char* message) {

        Block* newBlock;

        newBlock = new Block(
                _sentLength++,
                _sentHead,
                (*_sentHead).getHash(),
                message);

        (*newBlock).mine(_work);
        _sentHead = newBlock;
    }

    void BlockChain::addReceivedBlock(char* encryptedBlock, char* privateKey) {

        openSSL::RSA*   rsa = NULL;
        openSSL::BIO*   bp;
        Block*          newBlock;
        char*           plaintextBlock = NULL;
        unsigned char*  buffer = NULL;

        bp = openSSL::BIO_new_mem_buf(privateKey, -1);
        openSSL::PEM_read_bio_RSAPrivateKey(bp, &rsa, 0, 0);

        buffer = (unsigned char*) malloc(sizeof(char) * (RSA_DIGEST_LENGTH + 1));
        plaintextBlock = (char*) malloc(sizeof(char*) * (RSA_HEX_DIGEST_LENGTH + 1));

        rebit(buffer, encryptedBlock);

        openSSL::RSA_private_decrypt(
                RSA_DIGEST_LENGTH,
                (unsigned char*) buffer,
                (unsigned char*) plaintextBlock,
                rsa,
                RSA_PKCS1_OAEP_PADDING);

        if(_receivedHead == NULL){

            newBlock = new Block(plaintextBlock, NULL);

            if(newBlock->getVersion() != 0)
                handleErrors();

            _receivedHead = newBlock;
            _receivedLength++;
        }
        else{

            newBlock = new Block(plaintextBlock, _receivedHead);

            if(newBlock->getVersion() != _receivedLength + 1)
                handleErrors();

            if(strcmp(newBlock->getPreviousHash(), _receivedHead->getHash()) != 0)
                handleErrors();

            _receivedHead = newBlock;
            _receivedLength++;
        }

        openSSL::RSA_free(rsa);
        openSSL::BIO_free(bp);
        free(buffer);
        free(plaintextBlock);
    }

    void BlockChain::addPrevSentBlock(char* plaintextBlock) {

        Block*          newBlock;

        if(_sentHead == NULL){

            newBlock = new Block(plaintextBlock, NULL);

            if(newBlock->getVersion() != 0)
                handleErrors();

            _sentHead = newBlock;
            _sentLength++;
        }
        else{

            newBlock = new Block(plaintextBlock, _receivedHead);

            if(newBlock->getVersion() != _receivedLength + 1)
                handleErrors();

            if(strcmp(newBlock->getPreviousHash(), _sentHead->getHash()) != 0)
                handleErrors();

            _sentHead = newBlock;
            _sentLength++;
        }
    }

    char* BlockChain::toString() {

        return _sentHead->toString();
    }

    char* BlockChain::getEncryptedBlock(char* publicKey) {

        openSSL::RSA*   rsa = NULL;
        openSSL::BIO*   bp;
        char*           plaintextBlock;
        char*           buffer;
        char*           encryptedBlock;
        char*           p;

        plaintextBlock = _sentHead->toJSON();
        buffer = (char*) malloc(sizeof(char) * (RSA_DIGEST_LENGTH + 1));
        encryptedBlock = (char*) malloc(sizeof(char) * (RSA_HEX_DIGEST_LENGTH + 1));

        bp = openSSL::BIO_new_mem_buf(publicKey, -1); //strlen(publicKey)
        openSSL::PEM_read_bio_RSAPublicKey(bp, &rsa, 0, 0);

        // TODO int val = Error handle the sscanf results
        openSSL::RSA_public_encrypt(
                strlen(plaintextBlock),
                (unsigned char*) plaintextBlock,
                (unsigned char*) buffer,
                rsa,
                RSA_PKCS1_OAEP_PADDING);

        p = encryptedBlock;
        for(int i = 0; i < RSA_DIGEST_LENGTH; i++){

            sprintf(p, "%02x", (unsigned char) buffer[i]);
            p += 2;
        }
        encryptedBlock[RSA_HEX_DIGEST_LENGTH] = '\0';

        openSSL::RSA_free(rsa);
        openSSL::BIO_free(bp);
        free(buffer);
        free(plaintextBlock);

        return encryptedBlock;
    }

    Block* BlockChain::getLastSentBlock() {

        return _sentHead;
    }

    Block* BlockChain::getLastReceivedBlock() {

        return _receivedHead;
    }

    void BlockChain::rebit(unsigned char* buffer, char* encryptedBlock){

        int i = 0;
        for(int j = 0; j < RSA_HEX_DIGEST_LENGTH; j++){

            bool isOdd;
            unsigned char byte;

            isOdd = j % 2;

            switch(encryptedBlock[j]){
                case '0':
                    byte = 0x00;
                    break;
                case '1':
                    byte = 0x01;
                    break;
                case '2':
                    byte = 0x02;
                    break;
                case '3':
                    byte = 0x03;
                    break;
                case '4':
                    byte = 0x04;
                    break;
                case '5':
                    byte = 0x05;
                    break;
                case '6':
                    byte = 0x06;
                    break;
                case '7':
                    byte = 0x07;
                    break;
                case '8':
                    byte = 0x08;
                    break;
                case '9':
                    byte = 0x09;
                    break;
                case 'a':
                    byte = 0x0A;
                    break;
                case 'b':
                    byte = 0x0B;
                    break;
                case 'c':
                    byte = 0x0C;
                    break;
                case 'd':
                    byte = 0x0D;
                    break;
                case 'e':
                    byte = 0x0E;
                    break;
                case 'f':
                    byte = 0x0F;
                    break;
            }

            if(isOdd){

                buffer[i] = buffer[i] | byte;
                i++;
            }
            else {

                byte = ((byte & 0x0F) << 4);
                buffer[i] = byte;
            }
        }
    }

    void BlockChain::loadSSL() {

        openSSL::OpenSSL_add_all_algorithms();
        openSSL::ERR_load_BIO_strings();
        openSSL::ERR_load_crypto_strings();
    }

    void BlockChain::handleErrors() {

        //openSSL::ERR_print_errors_fp(stderr);
        //abort();
    }

    void BlockChain::BIOtoChar(
            openSSL::BIO*   bp,
            char**          key) {

        int length;

        length = BIO_pending(bp);
        *key = (char*) malloc(sizeof(char) * (length + 1));

        if(!(openSSL::BIO_read(bp, (unsigned char*) *key, length - 1)))
            handleErrors();

        (*key)[length - 1] = '\0';
    }

    void BlockChain::generateKeyPair(
            char**  publicKey,
            char**  privateKey) {

        openSSL::BIGNUM* e;
        openSSL::RSA*    rsa;
        openSSL::BIO*    bp_public;
        openSSL::BIO*    bp_private;

        if(!(e = openSSL::BN_new()))
            handleErrors();

        if(!(openSSL::BN_set_word(e, SSE)))
            handleErrors();

        if(!(rsa = openSSL::RSA_new()))
            handleErrors();

        if(!(openSSL::RSA_generate_multi_prime_key(rsa, RSA_BITS, RSA_PRIMES, e, NULL)))
            handleErrors();

        bp_public = openSSL::BIO_new(openSSL::BIO_s_mem());
        if(!(openSSL::PEM_write_bio_RSAPublicKey(bp_public, rsa)))
            handleErrors();

        bp_private = openSSL::BIO_new(openSSL::BIO_s_mem());
        if(!(openSSL::PEM_write_bio_RSAPrivateKey(bp_private, rsa, NULL, NULL, 0, NULL, NULL)))
            handleErrors();

        BIOtoChar(bp_public, publicKey);
        BIOtoChar(bp_private, privateKey);

        openSSL::BN_free(e);
        openSSL::RSA_free(rsa);
        openSSL::BIO_free(bp_private);
        openSSL::BIO_free(bp_public);
    }

    void BlockChain::generateChatId(
            char**  chatId,
            char*   localAddress,
            char*   foreignAddress) {

        openSSL::SHA512_CTX* ctx;
        char*   salt;
        char*   buffer;
        char*   p;
        int     saltLength;
        int     localAddressLength;
        int     foreignAddressLength;

        localAddressLength = strlen(localAddress);
        foreignAddressLength = strlen(foreignAddress);
        saltLength = 1023 + localAddressLength + foreignAddressLength;

        ctx = (openSSL::SHA512_CTX *) malloc(sizeof(openSSL::SHA512_CTX));
        salt = (char*) malloc(sizeof(char) * saltLength);
        buffer = (char*) malloc(sizeof(char) * (SHA512_DIGEST_LENGTH + 1));
        (*chatId) = (char*) malloc(sizeof(char) * (SHA512_HEX_DIGEST_LENGTH + 2));

        saltLength = sprintf(
                salt,
                "{%s_%s}",
                localAddress,
                foreignAddress);

        openSSL::SHA512_Init(ctx);
        openSSL::SHA512_Update(ctx, salt, saltLength);
        openSSL::SHA512_Final((unsigned char*) buffer, ctx);
        p = (*chatId);
        for(int i = 0; i < SHA512_DIGEST_LENGTH; i++){

            sprintf(p, "%02x", (unsigned char) buffer[i]);

            if(i != SHA512_DIGEST_LENGTH - 2)
                p += 2;
        }
        (*chatId)[SHA512_HEX_DIGEST_LENGTH] = '\0';

        free(buffer);
        free(salt);
        free(ctx);
    }

    void BlockChain::generateUserId(char** userId, char* localAddress) {

        openSSL::SHA512_CTX* ctx;
        char*   buffer;
        char*   p;
        int     localAddressLength;

        localAddressLength = strlen(localAddress);

        ctx = (openSSL::SHA512_CTX *) malloc(sizeof(openSSL::SHA512_CTX));
        buffer = (char*) malloc(sizeof(char) * (SHA512_DIGEST_LENGTH + 1));
        (*userId) = (char*) malloc(sizeof(char) * (SHA512_HEX_DIGEST_LENGTH + 2));

        openSSL::SHA512_Init(ctx);
        openSSL::SHA512_Update(ctx, localAddress, localAddressLength);
        openSSL::SHA512_Final((unsigned char*) buffer, ctx);
        p =  (*userId);
        for(int i = 0; i < SHA512_DIGEST_LENGTH; i++){

            sprintf(p, "%02x", (unsigned char) buffer[i]);

            if(i != SHA512_DIGEST_LENGTH - 2)
                p += 2;
        }
        (*userId)[SHA512_HEX_DIGEST_LENGTH] = '\0';

        free(buffer);
        free(ctx);
    }

    void BlockChain::freeBlockChain(BlockChain* blockChain){

        if(blockChain == NULL)
            return;

        Block::freeBlock(blockChain->_sentHead);
        Block::freeBlock(blockChain->_receivedHead);

        free(blockChain->_chatId);

        delete(blockChain);
    }
}
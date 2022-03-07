#include "blockChain.h"

namespace howl {

    BlockChain::BlockChain(char* chatId) {

        Block*  genBlock;
        char*   initialPreviousHash;
        char*   initialCurrentHash;
        char*   initialMerklerootHash;
        char*   initialMessage;

        initialPreviousHash = (char*) malloc(sizeof(char) * 5);
        initialCurrentHash = (char*) malloc(sizeof(char) * 5);
        initialMerklerootHash = (char*) malloc(sizeof(char) * 5);
        initialMessage = (char*) malloc(sizeof(char) * 12);
        _chatId = (char*) malloc(sizeof(char*) * (SHA512_HEX_DIGEST_LENGTH + 1));

        _sentLength = 0;
        _receivedLength = 0;
        _work = 3;

        sprintf(initialPreviousHash, "NULL");
        sprintf(initialCurrentHash, "NULL");
        sprintf(initialMerklerootHash, "NULL");
        sprintf(initialMessage, "GENSISBLOCK");
        sprintf(_chatId, "%s", chatId);

        genBlock = new Block(
                _sentLength++,
                NULL,
                initialPreviousHash,
                initialCurrentHash,
                initialMerklerootHash,
                initialMessage);

        (*genBlock).mine(_work);
        _sentHead = genBlock;
        _receivedHead = NULL;
    }

    void BlockChain::addSentBlock(char* message){

        Block* newBlock;

        newBlock = new Block(
                _sentLength++,
                _sentHead,
                (*_sentHead).getHash(),
                message);

        (*newBlock).mine(_work);
        _sentHead = newBlock;
    }

    void BlockChain::addReceivedBlock(char* encryptedBlock, char* privateKey){

        openSSL::RSA*   rsa = NULL;
        openSSL::BIO*   bp;
        Block*          newBlock;
        char*           plaintextBlock = NULL;

        bp = openSSL::BIO_new_mem_buf(privateKey, -1);
        openSSL::PEM_read_bio_RSAPrivateKey(bp, &rsa, 0, 0);

        plaintextBlock = (char*) malloc(sizeof(char*) * (RSA_DIGEST_LENGTH + 1));

        openSSL::RSA_private_decrypt(
                RSA_DIGEST_LENGTH ,
                (unsigned char*) encryptedBlock,
                (unsigned char*) plaintextBlock,
                rsa,
                RSA_PKCS1_OAEP_PADDING);

        if(_receivedHead == NULL){

            newBlock = new Block(plaintextBlock, NULL);

            if(newBlock->getVersion() != 0)
                std::cout << "\tVersion Failed" << std::endl;

            _receivedHead = newBlock;
        }
        else{

            newBlock = new Block(plaintextBlock, _receivedHead);

            if(newBlock->getVersion() != _receivedLength + 1)
                std::cout << "\tVersion Failed" << std::endl;

            if(strcmp(newBlock->getPreviousHash(), _receivedHead->getHash()) != 0)
                std::cout << "\tPrevious Hash Failed" << std::endl;

            _receivedHead = newBlock;
            _receivedLength++;
        }

        openSSL::RSA_free(rsa);
        openSSL::BIO_free(bp);
        //free(encryptedBlock);
        //free(plaintextBlock);
    }

    char* BlockChain::toString(){

        return _sentHead->toString();
    }

    char* BlockChain::getEncryptedBlock(char* publicKey){

        openSSL::RSA*   rsa = NULL;
        openSSL::BIO*   bp;
        char*           plaintextBlock;
        char*           buffer;
        char*           encryptedBlock;
        char*           p;

        plaintextBlock = _sentHead->toJSON();
        buffer = (char*) malloc(sizeof(char) * RSA_DIGEST_LENGTH);
        encryptedBlock = (char*) malloc(sizeof(char) * (RSA_HEX_DIGEST_LENGTH + 1));

        bp = openSSL::BIO_new_mem_buf(publicKey, -1); //strlen(publicKey)
        openSSL::PEM_read_bio_RSAPublicKey(bp, &rsa, 0, 0);

        buffer = (char*) malloc(sizeof(char*) * RSA_DIGEST_LENGTH);

        // TODO int val = Error handle the sscanf results
        openSSL::RSA_public_encrypt(
                strlen(plaintextBlock) + 1,  //TODO maybe + 1
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

        OpenSSL::RSA_free(rsa);
        OpenSSL::BIO_free(bp);
        //free(plaintextBlock);

        return buffer;
    }

    void BlockChain::loadSSL(){

        openSSL::OpenSSL_add_all_algorithms();
        openSSL::ERR_load_BIO_strings();
        openSSL::ERR_load_crypto_strings();
    }

    void BlockChain::handleErrors(){

        //openSSL::ERR_print_errors_fp(stderr);
        //abort();
    }

    void BlockChain::BIOtoChar(
            openSSL::BIO*   bp,
            char**          key){

        int length;

        length = BIO_pending(bp);
        *key = (char*) malloc(sizeof(char) * (length + 1));

        if(!(openSSL::BIO_read(bp, (unsigned char*) *key, length)))
            handleErrors();
    }

    void BlockChain::generateKeyPair(
            char**  publicKey,
            char**  privateKey){

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
            char**  chatID,
            char*   localAddress,
            char*   foreignAddress){

    }

    Block* BlockChain::getLastSentBlock() {

        return _sentHead;
    }

    Block* BlockChain::getLastReceivedBlock() {

        return _receivedHead;
    }

}
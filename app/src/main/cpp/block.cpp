
#include "block.h"

namespace howl {

    Block::Block(
            uint32_t index,
            Block* previousBlock,
            char* previousHash,
            char* message):

            _version(index),
            _previousBlock(previousBlock),
            _previousHash(previousHash),
            _message(message) {

        _currentHash = nullptr;
        _merklerootHash = nullptr;
        _nonce = 0;
        _timeSent = duration_cast<std::chrono::milliseconds>(std::chrono::system_clock::now().time_since_epoch()).count();
        _timeRecieved = 0;
    }

    Block::Block(
            uint32_t index,
            Block* previousBlock,
            char* previousHash,
            char* currentHash,
            char* merklerootHash,
            char* message):

            _version(index),
            _previousBlock(previousBlock),
            _previousHash(previousHash),
            _currentHash(currentHash),
            _merklerootHash(merklerootHash),
            _message(message) {

        _nonce = 0;
        _timeSent = duration_cast<std::chrono::milliseconds>(std::chrono::system_clock::now().time_since_epoch()).count();
        _timeRecieved = 0;
    }

    Block::Block(char* plaintextBlock, Block* previousBlock) {

        char* buffer;

        buffer = (char*) malloc(sizeof(char) * 1000);
        _previousHash = (char*) malloc(sizeof(char) * 1000);
        _message = (char*) malloc(sizeof(char) * 1000);

        // TODO int val = Error handle the sscanf results
        sscanf(
                plaintextBlock,
                "{\n\t\"version\":%d\n\t\"nonce\":%d\n\t\"previousHash\":\"%[^\"]\"\n\t\"message\":\"%[^\"]\"\n\t\"time\":%ld\n}",
                &_version,
                &_nonce,
                _previousHash,
                _message,
                &_timeSent);

        _previousBlock = previousBlock;
        _timeRecieved = duration_cast<std::chrono::milliseconds>(std::chrono::system_clock::now().time_since_epoch()).count();

        _calculateMerklerootHash();
        _calculateHash();

        //free(buffer);
    }

    uint32_t Block::getVersion() {

        return _version;
    }

    char* Block::getPreviousHash() {

        return _previousHash;
    }

    char* Block::getHash() {

        return _currentHash;
    }

    char* Block::toString() {

        char* buffer;

        buffer = (char*) malloc(sizeof(char) * 2048);

        sprintf(
                buffer,
                "{\n\t\"version\":%d\n\t\"nonce\":%d\n\t\"previousHash\":\"%s\"\n\t\"currentHash\":\"%s\"\n\t\"merklerootHash\":\"%s\"\n\t\"message\":\"%s\"\n\t\"time\":%ld\n}",
                _version,
                _nonce,
                _previousHash,
                _currentHash,
                _merklerootHash,
                _message,
                _timeSent);

        return buffer;
    }

    char* Block::toJSON(){

        char* buffer;

        buffer = (char*) malloc(sizeof(char) * 1024);

        sprintf(
                buffer,
                "{\n\t\"version\":%d\n\t\"nonce\":%d\n\t\"previousHash\":\"%s\"\n\t\"message\":\"%s\"\n\t\"time\":%ld\n}",
                _version,
                _nonce,
                _previousHash,
                _message,
                _timeSent);

        return buffer;
    }

    void Block::mine(uint32_t work) {

        bool proofOfWork;

        _calculateMerklerootHash();
        proofOfWork = false;
        while(!proofOfWork){

            _nonce++;
            _calculateHash();
            proofOfWork = true;
            for(uint32_t i = 0; i < work; i++){

                if(_currentHash[i] != '0'){

                    proofOfWork = false;
                    //free(_currentHash);
                    break;
                }
            }
        }
    }

    int Block::_calculateHash() {

        openSSL::SHA512_CTX* ctx;
        char*   salt;
        char*   buffer;
        char*   p;
        int     saltLength;
        int     messageLength;
        int     previousHashLength;
        int     merklerootHashLength;

        messageLength = strlen(_message);
        previousHashLength = strlen(_previousHash);
        merklerootHashLength = strlen(_merklerootHash);
        saltLength = 100 + messageLength + // should be like 50? TODO fix padding later
                     previousHashLength + merklerootHashLength + 1;

        ctx = (openSSL::SHA512_CTX *) malloc(sizeof(openSSL::SHA512_CTX));
        salt = (char*) malloc(sizeof(char) * saltLength);
        buffer = (char*) malloc(sizeof(char) * (SHA512_DIGEST_LENGTH + 1));
        _currentHash = (char*) malloc(sizeof(char) * (SHA512_HEX_DIGEST_LENGTH + 8));

        saltLength = sprintf(
                salt,
                "%d%d%jd%s%s%s",
                _version,
                _nonce,
                _timeSent,
                _message,
                _previousHash,
                _merklerootHash);

        openSSL::SHA512_Init(ctx);
        openSSL::SHA512_Update(ctx, salt, saltLength);
        openSSL::SHA512_Final((unsigned char*) buffer, ctx);
        p =  _currentHash;
        for(int i = 0; i < SHA512_DIGEST_LENGTH; i++){

            sprintf(p, "%02x", (unsigned char) buffer[i]);

            if(i != SHA512_DIGEST_LENGTH - 2)
                p += 2;
        }
        _currentHash[SHA512_HEX_DIGEST_LENGTH] = '\0';

        //free(buffer);
        //free(salt);
        //free(ctx);

        return 1;
    }

    int Block::_calculateMerklerootHash() {

        openSSL::SHA512_CTX* ctx;
        Block*  iterator;
        char*   salt;
        char*   buffer;
        char*   p;
        int     i;

        ctx = (openSSL::SHA512_CTX *) malloc(sizeof(openSSL::SHA512_CTX));
        salt = (char*) malloc(sizeof(char) * (MERKLEROOT_SALT + 1));
        buffer = (char*) malloc(sizeof(char) * (SHA512_DIGEST_LENGTH + 1));
        _merklerootHash = (char*) malloc(sizeof(char) * (SHA512_HEX_DIGEST_LENGTH + 8));

        iterator = this;
        i = 0;
        while(true){

            int hashLength;

            if(iterator->_version == 0)
                break;

            if(iterator->_previousBlock == NULL)
                break;

            iterator = iterator->_previousBlock;

            hashLength = strlen(iterator->_currentHash);
            for(int j = 0; j < hashLength; j++){

                salt[i++] = iterator->_currentHash[j];
                if(i == MERKLEROOT_SALT)
                    break;
            }
        }
        salt[i] = '\0';

        openSSL::SHA512_Init(ctx);
        openSSL::SHA512_Update(ctx, salt, i);
        openSSL::SHA512_Final((unsigned char*) buffer, ctx);

        p = _merklerootHash;
        for(int i = 0; i < SHA512_DIGEST_LENGTH; i++){

            sprintf(p, "%02x", (unsigned char) buffer[i]);

            if(i != SHA512_DIGEST_LENGTH - 2)
                p += 2;
        }
        _merklerootHash[SHA512_HEX_DIGEST_LENGTH] = '\0';

        //free(buffer);
        //free(salt);
        //free(ctx);

        return 1;
    }
}
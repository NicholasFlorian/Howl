
#include <cstring>
#include <chrono>
#include <math.h>

namespace openSSL {
    #include <openssl/bio.h>
    #include <openssl/ossl_typ.h>
    #include <openssl/conf.h>
    #include <openssl/bn.h>
    #include <openssl/engine.h>
    #include <openssl/rsa.h>
    #include <openssl/evp.h>
    #include <openssl/sha.h>
    #include <openssl/err.h>
};

#define SHA512_DIGEST_LENGTH        64
#define SHA512_HEX_DIGEST_LENGTH    126
#define MAX_MESSAGE                 256
#define MERKLEROOT_SALT             65536

namespace howl{

    class Block {

    public:

        Block(
                uint32_t    nIndexIn,
                Block*      previousblock,
                char*       previousHash,
                char*       message);

        Block(
                uint32_t index,
                Block* previousBlock,
                char* previousHash,
                char* currentHash,
                char* merklerootHash,
                char* message);

        Block(char* plaintextBlock, Block* previousBlock);

        uint32_t    getVersion();
        char*       getPreviousHash();
        char*       getHash();
        char*       toString();
        char*       toJSON();
        void        mine(uint32_t  work);

    protected:

        int         _calculateMerklerootHash();
        int         _calculateHash();

    private:

        uint32_t    _version;
        uint32_t    _nonce;
        Block*      _previousBlock;
        char*       _previousHash;
        char*       _currentHash;
        char*       _merklerootHash;
        char*       _message;
        time_t      _timeSent;
        time_t      _timeRecieved;

    };
}
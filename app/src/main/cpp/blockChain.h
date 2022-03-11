
#include "block.h"

#define SSE                         RSA_F4
#define RSA_BITS                    4096
#define RSA_DIGEST_LENGTH           512
#define RSA_HEX_DIGEST_LENGTH       1024
#define RSA_PRIMES                  2

namespace howl {

    class BlockChain {

    public:

        BlockChain(char* chatId);

        void        buildSentBlock(char* message);
        void        addReceivedBlock(char* encryptedBlock, char* privateKey);
        void        addPrevSentBlock(char* encryptedBlock, char* privateKey);
        char*       toString();
        char*       getEncryptedBlock(char* publicKey);
        Block*      getLastSentBlock();
        Block*      getLastReceivedBlock();

        static void rebit(unsigned char* buffer, char* encryptedBlock);
        static void loadSSL();
        static void handleErrors();
        static void BIOtoChar(
                openSSL::BIO*   bp,
                char**          key);
        static void generateKeyPair(
                char**  publicKey,
                char**  privateKey);
        static void generateChatId(
                char**  chatID,
                char*   localAddress,
                char*   foreignAddress);
        static void generateUserId(char** userId, char* localAddress);

    private:

        char*       _chatId;
        uint32_t    _work;
        uint32_t    _sentLength;
        uint32_t    _receivedLength;
        Block*      _sentHead;
        Block*      _receivedHead;
    };
}